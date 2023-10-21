package io.future.laboratories.anilistbingo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import io.future.laboratories.Companion.TEMP_PATH
import io.future.laboratories.Companion.bingoStoragePath
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistbingo.Options.Companion.PINNED_CATEGORY
import io.future.laboratories.anilistbingo.Options.Companion.SHOW_FINISHED_ANIME
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

    // compose var
    private var currentPage: Page by mutableStateOf(Page.BINGO_OVERVIEW())
    private var isLoggedIn: Boolean by mutableStateOf(false)
    private var runtimeAPIData: APIController.RuntimeData by mutableStateOf(
        APIController.RuntimeData(
            dataFetchCompleted = false,
            initialRuntimeAniListData = null,
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        val apiController = APIController(
            preferences = preferences,
        )

        val options = Options.getInstance(
            preferences = preferences,
        )

        runtimeAPIData = APIController.RuntimeData(
            dataFetchCompleted = savedInstanceState?.getBoolean(BUNDLE_IS_FETCHED) ?: false,
            initialRuntimeAniListData = loadSingle(TEMP_PATH),
        )

        installSplashScreen().setKeepOnScreenCondition {
            return@setKeepOnScreenCondition !runtimeAPIData.dataFetchCompleted
        }

        super.onCreate(savedInstanceState)

        with(apiController) {
            intent.data?.processFragmentData(runtimeAPIData)

            isLoggedIn = validateKey()

            runtimeAPIData.fetchAniList()
        }

        val dropDownItems = arrayOf(
            DropDownItemData(
                textId = { R.string.options },
                contentDescription = null,
                imageVector = Icons.Rounded.Settings,
                isVisible = { currentPage !is Page.OPTIONS },
                onClick = {
                    currentPage = Page.OPTIONS(sourcePage = currentPage)
                },
            ),
            DropDownItemData(
                textId = { if (isLoggedIn) R.string.logout else R.string.login },
                contentDescription = null,
                imageVector = Icons.Rounded.AccountCircle,
                isVisible = { true },
                onClick = {
                    if (isLoggedIn) {
                        preferences.logout(this@MainActivity)

                        runtimeAPIData.runtimeAniListData = null
                        isLoggedIn = false
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
                    titleId = { currentPage.nameResId },
                    isNavVisible = { currentPage !is Page.BINGO_OVERVIEW && currentPage !is Page.EDITOR },
                    onNavPress = { currentPage = currentPage.previousPage },
                    profilePictureUrl = runtimeAPIData.runtimeAniListData?.user?.avatar?.medium,
                    isLoggedIn = isLoggedIn,
                    dropDownItems = dropDownItems,
                ) {
                    when (currentPage) {
                        is Page.ANIME_OVERVIEW -> AnimeOverviewPage(
                            bingoData = (currentPage as Page.ANIME_OVERVIEW).bingoData,
                            showFinished = options[SHOW_FINISHED_ANIME],
                            pinned = options[PINNED_CATEGORY],
                            animeDataList = runtimeAPIData.runtimeAniListData?.mediaListCollection,
                            mediaTags = runtimeAPIData.runtimeAniListData?.mediaTagCollection,
                            onSelectAnime = { bingoData, animeData ->
                                currentPage = Page.BINGO(
                                    bingoData = bingoData,
                                    animeData = animeData,
                                    sourcePage = currentPage,
                                )
                            },
                        )

                        is Page.BINGO_OVERVIEW -> BingoOverviewPage(
                            bingoDataList = runtimeData,
                            onEdit = { data ->
                                currentPage = Page.EDITOR(
                                    bingoData = data,
                                    sourcePage = currentPage,
                                )
                            },
                            onDelete = { data ->
                                deleteSingle(bingoStoragePath("${data.id}"))
                                runtimeData.remove(data)
                            },
                            onSelectBingo = { bingoData ->
                                currentPage = Page.ANIME_OVERVIEW(
                                    bingoData = bingoData,
                                    sourcePage = currentPage,
                                )
                            },
                        )

                        is Page.BINGO -> BingoPage(
                            context = this@MainActivity,
                            bingoData = (currentPage as Page.BINGO).bingoData,
                            animeData = (currentPage as Page.BINGO).animeData,
                        )

                        is Page.EDITOR -> {
                            EditorPage(
                                preferences = preferences,
                                bingoData = (currentPage as Page.EDITOR).bingoData,
                                onBackButtonPress = {
                                    currentPage = Page.BINGO_OVERVIEW()
                                },
                                onClickSave = { bingoData, isNew ->
                                    save(bingoData, bingoStoragePath("${bingoData.id}"))
                                    if (isNew) {
                                        runtimeData.add(bingoData)
                                    }

                                    currentPage = Page.BINGO_OVERVIEW()
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(BUNDLE_IS_FETCHED, runtimeAPIData.dataFetchCompleted)
        save(runtimeAPIData.runtimeAniListData, TEMP_PATH)

        super.onSaveInstanceState(outState)
    }

    @Suppress("ClassName")
    private sealed class Page(@StringRes val nameResId: Int, private var sourcePage: Page?) {
        val previousPage
            get() = previousPage()

        private fun previousPage(): Page = sourcePage ?: BINGO_OVERVIEW()

        class BINGO_OVERVIEW(sourcePage: Page? = null) :
            Page(R.string.overview_bingo, sourcePage)

        class ANIME_OVERVIEW(val bingoData: BingoData, sourcePage: Page) :
            Page(R.string.overview_anime, sourcePage)

        class EDITOR(val bingoData: BingoData? = null, sourcePage: Page) :
            Page(R.string.editor, sourcePage)

        class BINGO(val bingoData: BingoData, val animeData: MediaList, sourcePage: Page) :
            Page(R.string.bingo, sourcePage)

        class OPTIONS(sourcePage: Page) : Page(R.string.options, sourcePage)
    }

    private companion object {
        private const val BUNDLE_IS_FETCHED = "IS_FETCHED"
        private const val PREFERENCE_BASE_KEY = "BINGO_PREFERENCE_KEY"
    }
}