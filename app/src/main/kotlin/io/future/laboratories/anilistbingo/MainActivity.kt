package io.future.laboratories.anilistbingo

import android.content.Intent
import android.content.SharedPreferences
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
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import io.future.laboratories.Companion.PREFERENCE_ACCESS_EXPIRED
import io.future.laboratories.Companion.PREFERENCE_ACCESS_TOKEN
import io.future.laboratories.Companion.PREFERENCE_ACCESS_TYPE
import io.future.laboratories.Companion.PREFERENCE_USER_ID
import io.future.laboratories.Companion.TEMP_PATH
import io.future.laboratories.Companion.storagePath
import io.future.laboratories.anilistapi.API
import io.future.laboratories.anilistapi.api
import io.future.laboratories.anilistapi.data.AniListBody
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistapi.data.MediaListCollectionAndUserData
import io.future.laboratories.anilistapi.enqueue
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
    // val
    private val preferences: SharedPreferences by lazy {
        getSharedPreferences(
            PREFERENCE_BASE_KEY,
            MODE_PRIVATE,
        )
    }
    private val authorization
        get() = "" +
                "${preferences.getString(PREFERENCE_ACCESS_TYPE, null)} " +
                "${preferences.getString(PREFERENCE_ACCESS_TOKEN, null)}" +
                ""

    // compose val
    private val runtimeData: SnapshotStateList<BingoData> by lazy { loadAllBingoData() }

    // var
    private var dataFetchCompleted: Boolean = false

    // compose var
    private var currentPage: Page by mutableStateOf(Page.OVERVIEW())
    private var isLoggedIn: Boolean by mutableStateOf(false)
    private var runtimeAniListData: MediaListCollectionAndUserData? by mutableStateOf(null)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        dataFetchCompleted = savedInstanceState?.getBoolean(BUNDLE_IS_FETCHED) ?: false
        runtimeAniListData = loadSingle(TEMP_PATH)

        fun fetchAniList(forced: Boolean = false) {
            if (dataFetchCompleted && !forced) return

            val userId = preferences.getLong(
                PREFERENCE_USER_ID,
                -1L,
            )

            if (userId == -1L) {
                dataFetchCompleted = true
                return
            }

            api.postAniList(
                authorization = authorization,
                json = AniListBody(
                    query = API.aniListListQuery,
                    variables = mapOf(
                        "userId" to userId,
                    )
                ),
            ).enqueue(onFailure = { _, _ -> dataFetchCompleted = true }) { _, listResponse ->
                runtimeAniListData = listResponse.body()?.data

                dataFetchCompleted = true
            }
        }

        installSplashScreen().setKeepOnScreenCondition {
            return@setKeepOnScreenCondition !dataFetchCompleted
        }

        super.onCreate(savedInstanceState)

        intent.data?.fragment?.let {
            preferences.edit {
                val sub1 = it.substringAfter("access_token=")
                putString(PREFERENCE_ACCESS_TOKEN, sub1.substringBefore("&"))
                val sub2 = it.substringAfter("&token_type=")
                putString(PREFERENCE_ACCESS_TYPE, sub2.substringBefore("&"))
                val sub3 = it.substringAfter("&expires_in=").substringBefore("&")
                putLong(PREFERENCE_ACCESS_EXPIRED, System.currentTimeMillis() + sub3.toInt() * 1000)
            }

            api.postAniListUser(
                authorization = authorization,
                json = AniListBody(
                    API.aniListUserQuery,
                    emptyMap(),
                ),
            ).enqueue { _, userResponse ->
                preferences.edit {
                    putLong(PREFERENCE_USER_ID, userResponse.body()?.data?.viewer?.id ?: -1L)
                }

                fetchAniList(forced = true)
            }
        }

        validateKey()

        fetchAniList()

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
                                    url = runtimeAniListData?.user?.avatar?.medium,
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
                                                preferences.logout(this@MainActivity)
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
                                animeDataList = runtimeAniListData?.mediaListCollection,
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
                                    preferences = preferences,
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(BUNDLE_IS_FETCHED, dataFetchCompleted)
        save(runtimeAniListData, TEMP_PATH)

        super.onSaveInstanceState(outState)
    }

    private fun validateKey() {
        isLoggedIn =
            System.currentTimeMillis() <= preferences.getLong(PREFERENCE_ACCESS_EXPIRED, -1L)
        if (!isLoggedIn) {
            preferences.logout(context = this)
        }
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