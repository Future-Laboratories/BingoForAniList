package io.future.laboratories.anilistbingo

import android.app.Activity
import android.content.Intent
import android.content.pm.ShortcutInfo.SHORTCUT_CATEGORY_CONVERSATION
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Commit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import io.future.laboratories.Companion.PREFERENCE_ACCESS_EXPIRED
import io.future.laboratories.Companion.TEMP_PATH
import io.future.laboratories.Companion.bingoStoragePath
import io.future.laboratories.anilistapi.api
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistapi.data.ScoreFormat
import io.future.laboratories.anilistbingo.Options.Companion.PINNED_CATEGORY
import io.future.laboratories.anilistbingo.Options.Companion.SCORING_SYSTEM
import io.future.laboratories.anilistbingo.Options.Companion.SHOW_FINISHED_ANIME
import io.future.laboratories.anilistbingo.Options.Companion.USE_CARDS
import io.future.laboratories.anilistbingo.Options.Companion.USE_GRADIENT
import io.future.laboratories.anilistbingo.controller.APIController
import io.future.laboratories.anilistbingo.controller.ShareController
import io.future.laboratories.anilistbingo.controller.ShareController.receive
import io.future.laboratories.common.BingoData
import io.future.laboratories.common.StyleProvider
import io.future.laboratories.ui.CustomScaffold
import io.future.laboratories.ui.DropDownItemData
import io.future.laboratories.ui.components.BooleanOption
import io.future.laboratories.ui.components.DropdownOption
import io.future.laboratories.ui.components.OptionGroup
import io.future.laboratories.ui.pages.AnimeOverviewPage
import io.future.laboratories.ui.pages.BingoOverviewPage
import io.future.laboratories.ui.pages.BingoPage
import io.future.laboratories.ui.pages.EditorPage
import io.future.laboratories.ui.pages.OptionsPage
import io.future.laboratories.ui.pages.AnimeBrowserPage
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
        val apiController = APIController.getInstance(
            preferences = preferences,
            onNetworkError = ::errorCodeHandle,
        )

        val options = Options.getInstance(
            preferences = preferences,
            controller = apiController,
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

        val dropDownItems = createDropDownItemData(
            apiController = apiController,
            viewModel = viewModel,
        )

        syncOptions(
            options = options,
            viewModel = viewModel,
        )

        setContent {
            AniListBingoTheme {
                window.statusBarColor = StyleProvider.gradientColor.toArgb()
                CustomScaffold(
                    titleId = { viewModel.currentPage.nameResId },
                    isNavVisible = { viewModel.currentPage !is Page.BINGO_OVERVIEW },
                    onNavPress = {
                        viewModel.onBackPress()
                    },
                    profilePictureUrl = viewModel.runtimeAPIData.runtimeAniListData?.user?.avatar?.medium,
                    isLoggedIn = viewModel.isLoggedIn,
                    dropDownItems = dropDownItems,
                ) {
                    with(viewModel.currentPage) {
                        when (this) {
                            is Page.ANIME_OVERVIEW -> AnimeOverviewPage(
                                bingoData = bingoData,
                                showFinished = options[SHOW_FINISHED_ANIME],
                                useCards = options[USE_CARDS],
                                pinned = options[PINNED_CATEGORY],
                                animeDataList = viewModel.runtimeAPIData.runtimeAniListData?.mediaListCollection,
                                mediaTags = viewModel.runtimeAPIData.runtimeAniListData?.mediaTagCollection,
                                scoreFormat = ScoreFormat.entries.first {
                                    it.value == options.get<DropdownOption>(
                                        SCORING_SYSTEM
                                    ).currentValue
                                },
                                onRefresh = {
                                    viewModel.fetchAPIData(
                                        apiController = apiController,
                                        activity = this@MainActivity,
                                        forced = true,
                                    )
                                },
                                onCommit = apiController::mutateEntry,
                                onClickDelete = { bingoData, animeData ->
                                    val deletionSuccessful = deleteSingle(
                                        bingoPath(
                                            bingoData = bingoData,
                                            animeData = animeData,
                                        )
                                    )

                                    val toastText = when (deletionSuccessful) {
                                        Status.Success -> R.string.delete_success
                                        Status.FileNotExist -> R.string.delete_file_error
                                        Status.PermissionDenied -> R.string.delete_permission_error
                                    }

                                    defaultToast(getString(toastText))
                                },
                                onSelectAnime = { bingoData, animeData ->
                                    viewModel.currentPage = Page.BINGO(
                                        bingoData = bingoData,
                                        animeData = animeData,
                                        sourcePage = viewModel.currentPage,
                                    )
                                },
                                onFABClick = {
                                    viewModel.currentPage =
                                        Page.ANIME_BROWSER(viewModel.currentPage)
                                }
                            )

                            is Page.BINGO_OVERVIEW -> BingoOverviewPage(
                                useCards = options[USE_CARDS],
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
                                    deleteAllBingoData(bingoData.id)
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
                                    storagePath = bingoPath(),
                                ) ?: bingoData,
                                onDataChange = { bingoData ->
                                    save(
                                        bingoData,
                                        bingoPath(),
                                    )
                                },
                            )

                            is Page.EDITOR -> EditorPage(
                                preferences = preferences,
                                bingoData = bingoData,
                                isImported = isImported,
                                showDialog = showDialog,
                                onBackDialogDismiss = {
                                    showDialog = false
                                },
                                onBackDialogAccept = {
                                    showDialog = false

                                    viewModel.currentPage = viewModel.currentPage.previousPage
                                },
                                onClickSave = { bingoData, isNew ->
                                    if (isNew) {
                                        runtimeData.add(bingoData)
                                    }

                                    save(bingoData, bingoStoragePath("${bingoData.id}"))

                                    viewModel.currentPage = Page.BINGO_OVERVIEW()
                                },
                            )

                            is Page.OPTIONS -> OptionsPage(
                                timestamp = preferences.getLong(PREFERENCE_ACCESS_EXPIRED, 0L),
                                version = BuildConfig.VERSION_NAME,
                                options = arrayOf(
                                    OptionGroup(
                                        text = stringResource(id = R.string.options_general),
                                        options = listOfNotNull(
                                            options[SHOW_FINISHED_ANIME],
                                            options[PINNED_CATEGORY],
                                        )
                                    ),
                                    OptionGroup(
                                        text = stringResource(id = R.string.options_appearance),
                                        options = listOfNotNull(
                                            options[USE_CARDS],
                                            options[USE_GRADIENT]
                                        )
                                    ),
                                    OptionGroup(
                                        text = stringResource(id = R.string.options_account),
                                        options = listOfNotNull(
                                            options[SCORING_SYSTEM],
                                        )
                                    )
                                ),
                            )

                            is Page.ANIME_BROWSER -> AnimeBrowserPage(
                                pages = viewModel.runtimeAPIData.runtimePages,
                                onRequestMore = { page ->
                                    with(apiController) {
                                        viewModel.runtimeAPIData.fetchNewPage(page = page)
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createDropDownItemData(
        apiController: APIController,
        viewModel: AniListBingoViewModel,
    ) = arrayOf(
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
            textId = { R.string.donate },
            contentDescription = null,
            imageVector = Icons.Rounded.Favorite,
            isVisible = { true },
            onClick = {
                val url = getString(R.string.donation_url)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(Uri.parse(url))
                startActivity(
                    intent,
                    null,
                )
            },
        ),
        DropDownItemData(
            textId = { R.string.github },
            contentDescription = null,
            imageVector = Icons.Rounded.Code,
            isVisible = { true },
            onClick = {
                val url = getString(R.string.github_url)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(Uri.parse(url))
                startActivity(
                    intent,
                    null,
                )
            },
        ),
        DropDownItemData(
            textId = { if (viewModel.isLoggedIn) R.string.logout else R.string.login },
            contentDescription = null,
            imageVector = Icons.Rounded.AccountCircle,
            isVisible = { true },
            onClick = {
                if (viewModel.isLoggedIn) {
                    preferences.logout(this)

                    viewModel.runtimeAPIData.runtimeAniListData = null
                    viewModel.isLoggedIn = false
                } else {
                    with(apiController) {
                        createLoginIntent()
                    }
                }
            },
        ),
    )

    /**
     * Sync Option with AniList API & StyleProvider
     */
    private fun syncOptions(
        options: Options,
        viewModel: AniListBingoViewModel,
    ) {
        // API
        viewModel.runtimeAPIData.runtimeAniListData?.user?.let {
            options.get<DropdownOption>(SCORING_SYSTEM).currentValue =
                it.mediaListOptions?.scoreFormat?.value.orEmpty()
        }

        // StyleProvider
        StyleProvider.useGradient = options.get<BooleanOption>(USE_GRADIENT).currentValue
        StyleProvider.useCards = options.get<BooleanOption>(USE_CARDS).currentValue
    }

    private fun AniListBingoViewModel.onBackPress() = with(currentPage) {
        onBackPress()
    }

    private fun setupBackpressHandle(viewModel: AniListBingoViewModel) {
        val callback = object : OnBackPressedCallback(
            enabled = true,
        ) {
            override fun handleOnBackPressed() {
                if (viewModel.currentPage !is Page.BINGO_OVERVIEW) {
                    viewModel.onBackPress()
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

internal class AniListBingoViewModel : ViewModel() {
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

        // If The Apps open from a shared Bingo, open in EditorView
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
        } else if (intent.categories?.contains(SHORTCUT_CATEGORY_CONVERSATION) == true) {
            currentPage = Page.EDITOR(
                bingoData = null,
                isImported = false,
                sourcePage = null,
            )
        } else {
            // If the app got open from OAuth, process data
            intent.data?.let {
                processUriData(
                    uri = it,
                    data = runtimeAPIData,
                )
            }
        }

        // Validate if login is (still) valid
        isLoggedIn = activity.validateKey()

        // Fetch data from AniList and save into TMP_PATH for offline functionality
        fetchAPIData(
            apiController = apiController,
            activity = activity,
        )
    }

    internal fun fetchAPIData(
        apiController: APIController,
        activity: Activity,
        forced: Boolean = false,
    ) = with(apiController) {
        runtimeAPIData.fetchAniList(forced = forced) {
            activity.save(it, TEMP_PATH)
        }
    }
}

@Suppress("ClassName")
internal sealed class Page(@StringRes val nameResId: Int, private val sourcePage: Page?) {
    internal val previousPage
        get() = sourcePage ?: BINGO_OVERVIEW()

    internal open fun AniListBingoViewModel.onBackPress() {
        currentPage = currentPage.previousPage
    }

    class BINGO_OVERVIEW(sourcePage: Page? = null) : Page(R.string.overview_bingo, sourcePage)

    class ANIME_OVERVIEW(
        val bingoData: BingoData,
        sourcePage: Page,
    ) : Page(R.string.overview_anime, sourcePage), SavableBingo

    class EDITOR(
        val bingoData: BingoData? = null,
        val isImported: Boolean = false,
        sourcePage: Page?,
    ) : Page(R.string.editor, sourcePage) {
        var showDialog: Boolean by mutableStateOf(false)

        override fun AniListBingoViewModel.onBackPress() {
            showDialog = true
        }
    }

    class BINGO(
        val bingoData: BingoData,
        val animeData: MediaList,
        sourcePage: Page,
    ) : Page(R.string.bingo, sourcePage), SavableBingo {
        fun bingoPath() = bingoPath(this.bingoData, this.animeData)
    }

    class OPTIONS(sourcePage: Page) : Page(R.string.options, sourcePage)

    class ANIME_BROWSER(sourcePage: Page) : Page(R.string.explorer_anime, sourcePage)

    sealed interface SavableBingo {
        fun bingoPath(bingoData: BingoData, animeData: MediaList) =
            "${animeData.media.id}/${bingoData.id}"
    }
}
