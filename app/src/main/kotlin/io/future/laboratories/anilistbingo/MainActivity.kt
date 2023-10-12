package io.future.laboratories.anilistbingo

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import io.future.laboratories.Companion.PREFERENCE_ACCESS_EXPIRED
import io.future.laboratories.Companion.PREFERENCE_ACCESS_TOKEN
import io.future.laboratories.Companion.PREFERENCE_ACCESS_TYPE
import io.future.laboratories.Companion.PREFERENCE_ACCESS_USER_ID
import io.future.laboratories.Companion.TEMP_PATH
import io.future.laboratories.Companion.storagePath
import io.future.laboratories.anilistapi.API
import io.future.laboratories.anilistapi.api
import io.future.laboratories.anilistapi.data.AniListBody
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistapi.data.MediaListCollectionData
import io.future.laboratories.anilistapi.enqueue
import io.future.laboratories.anilistbingo.MainActivity.Page.OVERVIEW.previousPage
import io.future.laboratories.anilistbingo.data.BingoData
import io.future.laboratories.common.deleteSingle
import io.future.laboratories.common.loadAllBingoData
import io.future.laboratories.common.loadSingle
import io.future.laboratories.common.logout
import io.future.laboratories.common.save
import io.future.laboratories.common.textColor
import io.future.laboratories.ui.components.DefaultSpacer
import io.future.laboratories.ui.components.PositiveButton
import io.future.laboratories.ui.theme.AniListBingoTheme

public class MainActivity : ComponentActivity() {
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

    private val runtimeData: SnapshotStateList<BingoData> by lazy { loadAllBingoData() }

    private var dataFetchCompleted: Boolean = false

    private var currentPage: Page by mutableStateOf(Page.OVERVIEW)
    private var isLoggedIn: Boolean by mutableStateOf(false)
    private var runtimeAniListData: MediaListCollectionData? by mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        dataFetchCompleted = savedInstanceState?.getBoolean(BUNDLE_IS_FETCHED) ?: false
        runtimeAniListData = loadSingle(TEMP_PATH)

        fun fetchAniList(forced: Boolean = false) {
            if (dataFetchCompleted && !forced) return

            val userId = preferences.getLong(
                PREFERENCE_ACCESS_USER_ID,
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
                    putLong(PREFERENCE_ACCESS_USER_ID, userResponse.body()?.data?.viewer?.id ?: -1L)
                }

                fetchAniList(forced = true)
            }
        }

        validateKey()

        fetchAniList()

        setContent {
            AniListBingoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    ) {
                        Column {
                            when (currentPage) {
                                is Page.OVERVIEW -> io.future.laboratories.ui.pages.OverviewPage(
                                    context = this@MainActivity,
                                    preferences = preferences,
                                    bingoDataList = runtimeData,
                                    animeDataList = runtimeAniListData?.mediaListCollection,
                                    isLoggedIn = isLoggedIn,
                                    onLogout = {
                                        isLoggedIn = false
                                        runtimeAniListData = null
                                    },
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

                                is Page.BINGO -> io.future.laboratories.ui.pages.BingoPage(
                                    context = this@MainActivity,
                                    bingoData = (currentPage as Page.BINGO).bingoData,
                                    animeData = (currentPage as Page.BINGO).animeData,
                                )

                                is Page.EDITOR -> {
                                    io.future.laboratories.ui.pages.EditorPage(
                                        preferences = preferences,
                                        bingoData = (currentPage as Page.EDITOR).bingoData,
                                    ) { bingoData, isNew ->
                                        save(bingoData, storagePath("${bingoData.id}"))
                                        if (isNew) {
                                            runtimeData.add(bingoData)
                                        }

                                        currentPage = Page.OVERVIEW
                                    }
                                }
                            }

                            DefaultSpacer()

                            PositiveButton(onClick = {
                                currentPage = previousPage?.previousPage ?: Page.OVERVIEW
                            }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val backString = stringResource(id = R.string.back)
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowBack,
                                        contentDescription = backString,
                                        tint = textColor,
                                    )
                                    Text(text = backString)
                                }
                            }
                        }

                        if (currentPage is Page.OVERVIEW) {
                            FloatingActionButton(
                                modifier = Modifier
                                    .width(64.dp)
                                    .aspectRatio(1f)
                                    .align(Alignment.BottomEnd),
                                onClick = { currentPage = Page.EDITOR() },
                                shape = RoundedCornerShape(32.dp),
                                containerColor = MaterialTheme.colorScheme.primary,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = null,
                                    tint = textColor,
                                    modifier = Modifier
                                        .fillMaxSize(0.9f),
                                )
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

    private sealed class Page(val previousPage: Page? = OVERVIEW) {
        data object OVERVIEW : Page(null)

        class EDITOR(var bingoData: BingoData? = null) : Page()

        class BINGO(var bingoData: BingoData, var animeData: MediaList) : Page()
    }

    private companion object {
        private const val BUNDLE_IS_FETCHED = "IS_FETCHED"
        private const val PREFERENCE_BASE_KEY = "BINGO_PREFERENCE_KEY"
    }
}