package io.future.laboratories.anilistbingo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import io.future.laboratories.Companion.TEMP_PATH
import io.future.laboratories.Companion.bingoStoragePath
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistbingo.Options.Companion.PINNED_CATEGORY
import io.future.laboratories.anilistbingo.Options.Companion.SHOW_FINISHED_ANIME
import io.future.laboratories.anilistbingo.controller.APIController
import io.future.laboratories.anilistbingo.controller.ShareController
import io.future.laboratories.anilistbingo.controller.ShareController.receive
import io.future.laboratories.common.BingoData
import io.future.laboratories.common.deleteSingle
import io.future.laboratories.common.loadAllBingoData
import io.future.laboratories.common.loadSingle
import io.future.laboratories.common.logout
import io.future.laboratories.common.save
import io.future.laboratories.ui.CustomScaffold
import io.future.laboratories.ui.DropDownItemData
import io.future.laboratories.ui.components.OptionGroup
import io.future.laboratories.ui.pages.AnimeOverviewPage
import io.future.laboratories.ui.pages.BingoOverviewPage
import io.future.laboratories.ui.pages.BingoPage
import io.future.laboratories.ui.pages.EditorPage
import io.future.laboratories.ui.pages.OptionsPage
import io.future.laboratories.ui.theme.AniListBingoTheme


public class MainActivity : ComponentActivity() {
    private val preferences by lazy {
        getSharedPreferences(
            PREFERENCE_BASE_KEY,
            MODE_PRIVATE,
        )
    }

    // compose val
    private val runtimeData: SnapshotStateList<BingoData> by lazy { loadAllBingoData() }

    override fun onCreate(savedInstanceState: Bundle?) {
        val apiController = APIController(
            preferences = preferences,
        )

        val options = Options.getInstance(
            preferences = preferences,
        )

        val viewModel: AniListBingoViewModel by viewModels()

        viewModel.setupViewModelData(
            apiController = apiController,
            intent = intent,
            activity = this,
        )

        installSplashScreen().setKeepOnScreenCondition {
            return@setKeepOnScreenCondition !viewModel.runtimeAPIData.dataFetchCompleted
        }

        super.onCreate(savedInstanceState)

        setupBackpressHandle(viewModel)

        val dropDownItems = arrayOf(
            DropDownItemData(
                textId = { R.string.options },
                contentDescription = null,
                imageVector = Icons.Rounded.Settings,
                isVisible = { viewModel.currentPage !is Page.OPTIONS },
                onClick = {
                    viewModel.currentPage = Page.OPTIONS(sourcePage = viewModel.currentPage)
                },
            ),
            DropDownItemData(
                textId = { if (viewModel.isLoggedIn) R.string.logout else R.string.login },
                contentDescription = null,
                imageVector = Icons.Rounded.AccountCircle,
                isVisible = { true },
                onClick = {
                    if (viewModel.isLoggedIn) {
                        preferences.logout(this@MainActivity)

                        viewModel.runtimeAPIData.runtimeAniListData = null
                        viewModel.isLoggedIn = false
                    } else {
                        val url = getString(R.string.anilistUrl)
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setData(Uri.parse(url))
                        startActivity(
                            intent,
                            null,
                        )
                    }
                },
            ),
        )

        setContent {
            AniListBingoTheme {
                CustomScaffold(
                    titleId = { viewModel.currentPage.nameResId },
                    isNavVisible = { viewModel.currentPage !is Page.BINGO_OVERVIEW && viewModel.currentPage !is Page.EDITOR },
                    onNavPress = { viewModel.currentPage = viewModel.currentPage.previousPage },
                    profilePictureUrl = viewModel.runtimeAPIData.runtimeAniListData?.user?.avatar?.medium,
                    isLoggedIn = viewModel.isLoggedIn,
                    dropDownItems = dropDownItems,
                ) {
                    when (viewModel.currentPage) {
                        is Page.ANIME_OVERVIEW -> AnimeOverviewPage(
                            bingoData = (viewModel.currentPage as Page.ANIME_OVERVIEW).bingoData,
                            showFinished = options[SHOW_FINISHED_ANIME],
                            pinned = options[PINNED_CATEGORY],
                            animeDataList = viewModel.runtimeAPIData.runtimeAniListData?.mediaListCollection,
                            mediaTags = viewModel.runtimeAPIData.runtimeAniListData?.mediaTagCollection,
                            onSelectAnime = { bingoData, animeData ->
                                viewModel.currentPage = Page.BINGO(
                                    bingoData = bingoData,
                                    animeData = animeData,
                                    sourcePage = viewModel.currentPage,
                                )
                            },
                        )

                        is Page.BINGO_OVERVIEW -> BingoOverviewPage(
                            bingoDataList = runtimeData,
                            onShare = { bingoData ->
                                with(ShareController) {
                                    share(bingoData)
                                }
                            },
                            onEdit = { bingoData ->
                                viewModel.currentPage = Page.EDITOR(
                                    bingoData = bingoData,
                                    sourcePage = viewModel.currentPage,
                                )
                            },
                            onDelete = { bingoData ->
                                deleteSingle(bingoStoragePath("${bingoData.id}"))
                                runtimeData.remove(bingoData)
                            },
                            onSelectBingo = { bingoData ->
                                viewModel.currentPage = Page.ANIME_OVERVIEW(
                                    bingoData = bingoData,
                                    sourcePage = viewModel.currentPage,
                                )
                            },
                        )

                        is Page.BINGO -> BingoPage(
                            bingoData = loadSingle<BingoData>(
                                storagePath = (viewModel.currentPage as Page.BINGO).bingoPath,
                            ) ?: (viewModel.currentPage as Page.BINGO).bingoData,
                            onDataChange = { bingoData ->
                                save(bingoData, (viewModel.currentPage as Page.BINGO).bingoPath)
                            }
                        )

                        is Page.EDITOR -> {
                            EditorPage(
                                preferences = preferences,
                                bingoData = (viewModel.currentPage as Page.EDITOR).bingoData,
                                isImported = (viewModel.currentPage as Page.EDITOR).isImported,
                                onBackButtonPress = {
                                    viewModel.currentPage = Page.BINGO_OVERVIEW()
                                },
                                onClickSave = { bingoData, isNew ->
                                    save(bingoData, bingoStoragePath("${bingoData.id}"))
                                    if (isNew) {
                                        runtimeData.add(bingoData)
                                    }

                                    viewModel.currentPage = Page.BINGO_OVERVIEW()
                                },
                            )
                        }

                        is Page.OPTIONS -> OptionsPage(
                            options = arrayOf(
                                OptionGroup(
                                    text = stringResource(id = R.string.options_general),
                                    options = listOfNotNull(
                                        options[SHOW_FINISHED_ANIME],
                                        options[PINNED_CATEGORY],
                                    )
                                ),
                            )
                        )
                    }
                }
            }
        }
    }

    private fun setupBackpressHandle(viewModel: AniListBingoViewModel) {
        val callback = object : OnBackPressedCallback(
            enabled = true,
        ) {
            override fun handleOnBackPressed() {
                if (viewModel.currentPage !is Page.BINGO_OVERVIEW) {
                    viewModel.currentPage = viewModel.currentPage.previousPage
                } else {
                    isEnabled = false

                    finish()
                }
            }
        }

        onBackPressedDispatcher.addCallback(
            owner = this,
            onBackPressedCallback = callback,
        )
    }

    private companion object {
        private const val PREFERENCE_BASE_KEY = "BINGO_PREFERENCE_KEY"
    }
}

public class AniListBingoViewModel : ViewModel() {
    // compose var
    internal var currentPage: Page by mutableStateOf(Page.BINGO_OVERVIEW())
    internal var isLoggedIn: Boolean by mutableStateOf(false)
    internal var runtimeAPIData: APIController.RuntimeData by mutableStateOf(
        APIController.RuntimeData(
            dataFetchCompleted = false,
            initialRuntimeAniListData = null,
        )
    )

    internal fun setupViewModelData(
        apiController: APIController,
        intent: Intent,
        activity: Activity,
    ) = with(apiController) {
        runtimeAPIData = APIController.RuntimeData(
            dataFetchCompleted = runtimeAPIData.dataFetchCompleted,
            initialRuntimeAniListData = activity.loadSingle(TEMP_PATH),
        )

        intent.data?.processFragmentData(data = runtimeAPIData)

        isLoggedIn = activity.validateKey()

        runtimeAPIData.fetchAniList()

        if (intent.scheme == "content") {
            intent.data.let {
                if (it != null) {
                    currentPage = Page.EDITOR(
                        bingoData = activity.receive(it),
                        isImported = true,
                        sourcePage = currentPage,
                    )
                }
            }
        }
    }
}

@Suppress("ClassName")
internal sealed class Page(@StringRes val nameResId: Int, private var sourcePage: Page?) {
    val previousPage
        get() = previousPage()

    private fun previousPage(): Page = sourcePage ?: BINGO_OVERVIEW()

    class BINGO_OVERVIEW(sourcePage: Page? = null) : Page(R.string.overview_bingo, sourcePage)

    class ANIME_OVERVIEW(
        val bingoData: BingoData,
        sourcePage: Page,
    ) : Page(R.string.overview_anime, sourcePage)

    class EDITOR(
        val bingoData: BingoData? = null,
        val isImported: Boolean = false,
        sourcePage: Page,
    ) : Page(R.string.editor, sourcePage)

    class BINGO(
        val bingoData: BingoData,
        val animeData: MediaList,
        sourcePage: Page,
    ) : Page(R.string.bingo, sourcePage) {
        val bingoPath = "${animeData.media.id}/${bingoData.id}"
    }

    class OPTIONS(sourcePage: Page) : Page(R.string.options, sourcePage)
}