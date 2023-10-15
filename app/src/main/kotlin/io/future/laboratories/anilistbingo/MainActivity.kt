package io.future.laboratories.anilistbingo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import io.future.laboratories.Companion.PREFERENCE_ACCESS_EXPIRED
import io.future.laboratories.Companion.TEMP_PATH
import io.future.laboratories.Companion.storagePath
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistbingo.data.BingoData
import io.future.laboratories.common.deleteSingle
import io.future.laboratories.common.loadAllBingoData
import io.future.laboratories.common.loadSingle
import io.future.laboratories.common.logout
import io.future.laboratories.common.save
import io.future.laboratories.common.textColor
import io.future.laboratories.ui.components.DropdownRow
import io.future.laboratories.ui.components.ProfileButton
import io.future.laboratories.ui.pages.BingoPage
import io.future.laboratories.ui.pages.EditorPage
import io.future.laboratories.ui.pages.Mode
import io.future.laboratories.ui.pages.OptionsPage
import io.future.laboratories.ui.pages.OverviewPage
import io.future.laboratories.ui.theme.AniListBingoTheme

public class MainActivity : ComponentActivity() {
    // compose val
    private val runtimeData: SnapshotStateList<BingoData> by lazy { loadAllBingoData() }

    // compose var
    private var currentPage: Page by mutableStateOf(Page.OVERVIEW())
    private var isLoggedIn: Boolean by mutableStateOf(false)
    private var runtimeAPIData: APIController.RuntimeData by mutableStateOf(
        APIController.RuntimeData(
            false,
            null
        )
    )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val apiController = APIController(
            preferences = getSharedPreferences(
                PREFERENCE_BASE_KEY,
                MODE_PRIVATE,
            )
        )

        runtimeAPIData = APIController.RuntimeData(
            dataFetchCompleted = savedInstanceState?.getBoolean(BUNDLE_IS_FETCHED) ?: false,
            initialRuntimeAniListData = loadSingle(TEMP_PATH)
        )

        installSplashScreen().setKeepOnScreenCondition {
            return@setKeepOnScreenCondition !runtimeAPIData.dataFetchCompleted
        }

        super.onCreate(savedInstanceState)

        with(apiController) {
            intent.data?.processFragmentData(runtimeAPIData)

            validateKey()

            runtimeAPIData.fetchAniList()
        }

        setContent {
            AniListBingoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text(text = stringResource(id = currentPage.nameResId)) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = textColor,
                            ),
                            actions = {
                                var showMenu by remember { mutableStateOf(false) }
                                ProfileButton(
                                    url = runtimeAPIData.runtimeAniListData?.user?.avatar?.medium,
                                    isLoggedIn = isLoggedIn,
                                    onClick = {
                                        showMenu = !showMenu
                                    }
                                )

                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    if (currentPage !is Page.OPTIONS) {
                                        val optionsString = stringResource(id = R.string.options)
                                        DropdownRow(
                                            text = optionsString,
                                            imageVector = Icons.Rounded.Settings,
                                            contentDescription = stringResource(id = R.string.options),
                                            onClick = {
                                                currentPage =
                                                    Page.OPTIONS(previousPage = currentPage)
                                                showMenu = false
                                            },
                                        )
                                    }
                                    DropdownRow(
                                        text = stringResource(if (isLoggedIn) R.string.logout else R.string.login),
                                        imageVector = Icons.Rounded.AccountCircle,
                                        contentDescription = "",
                                        onClick = {
                                            if (isLoggedIn) {
                                                with(apiController) {
                                                    preferences.logout(this@MainActivity)
                                                }
                                                runtimeAPIData.runtimeAniListData = null
                                                isLoggedIn = false

                                                showMenu = false
                                            } else {
                                                val url = getString(R.string.anilistUrl)
                                                val intent = Intent(Intent.ACTION_VIEW)
                                                intent.setData(Uri.parse(url))
                                                startActivity(
                                                    intent,
                                                    null,
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        )
                    },
                    contentWindowInsets = WindowInsets(
                        left = 10.dp,
                        top = 10.dp,
                        right = 10.dp,
                        bottom = 10.dp,
                    ),
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(top = 10.dp),
                    ) {
                        when (currentPage) {
                            is Page.OVERVIEW -> OverviewPage(
                                bingoDataList = runtimeData,
                                animeDataList = runtimeAPIData.runtimeAniListData?.mediaListCollection,
                                defaultMode = (currentPage as Page.OVERVIEW).mode,
                                onEdit = { data ->
                                    currentPage = Page.EDITOR(bingoData = data)
                                },
                                onDelete = { data ->
                                    deleteSingle(storagePath("${data.id}"))
                                    runtimeData.remove(data)
                                },
                            ) { bingoData, animeData ->
                                currentPage = Page.BINGO(
                                    bingoData = bingoData,
                                    animeData = animeData,
                                )
                            }

                            is Page.BINGO -> BingoPage(
                                context = this@MainActivity,
                                bingoData = (currentPage as Page.BINGO).bingoData,
                                animeData = (currentPage as Page.BINGO).animeData,
                            ) { bingoData ->
                                currentPage = Page.OVERVIEW(
                                    mode = Mode.ANIME(
                                        bingoData = bingoData,
                                    )
                                )
                            }

                            is Page.EDITOR -> {
                                EditorPage(
                                    preferences = with(apiController) { preferences },
                                    bingoData = (currentPage as Page.EDITOR).bingoData,
                                    onBackButtonPress = {
                                        currentPage = Page.OVERVIEW()
                                    },
                                ) { bingoData, isNew ->
                                    save(bingoData, storagePath("${bingoData.id}"))
                                    if (isNew) {
                                        runtimeData.add(bingoData)
                                    }

                                    currentPage = Page.OVERVIEW()
                                }
                            }

                            is Page.OPTIONS -> OptionsPage {
                                currentPage = (currentPage as Page.OPTIONS).previousPage
                            }
                        }
                    }
                }
            }
        }
    }

    private fun APIController.validateKey() {
        isLoggedIn =
            System.currentTimeMillis() <= preferences.getLong(PREFERENCE_ACCESS_EXPIRED, -1L)
        if (!isLoggedIn) {
            preferences.logout(context = this@MainActivity)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(BUNDLE_IS_FETCHED, runtimeAPIData.dataFetchCompleted)
        save(runtimeAPIData.runtimeAniListData, TEMP_PATH)

        super.onSaveInstanceState(outState)
    }

    private sealed class Page(@StringRes val nameResId: Int) {
        class OVERVIEW(val mode: Mode = Mode.BINGO) : Page(R.string.overview)

        class EDITOR(var bingoData: BingoData? = null) : Page(R.string.editor)

        class BINGO(var bingoData: BingoData, var animeData: MediaList) : Page(R.string.bingo)

        class OPTIONS(val previousPage: Page) : Page(R.string.options)
    }

    private companion object {
        private const val BUNDLE_IS_FETCHED = "IS_FETCHED"
        private const val PREFERENCE_BASE_KEY = "BINGO_PREFERENCE_KEY"
    }
}