package io.future.laboratories.anilistbingo

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import io.future.laboratories.anilistbingo.Companion.PREFERENCE_ACCESS_EXPIRED
import io.future.laboratories.anilistbingo.Companion.PREFERENCE_ACCESS_TOKEN
import io.future.laboratories.anilistbingo.Companion.PREFERENCE_ACCESS_TYPE
import io.future.laboratories.anilistbingo.Companion.PREFERENCE_ACCESS_USER_ID
import io.future.laboratories.anilistbingo.data.BingoData
import io.future.laboratories.anilistbingo.data.api.API
import io.future.laboratories.anilistbingo.data.api.AniListBody
import io.future.laboratories.anilistbingo.pages.BingoPage
import io.future.laboratories.anilistbingo.pages.EditorPage
import io.future.laboratories.anilistbingo.pages.OverviewPage
import io.future.laboratories.anilistbingo.ui.PositiveButton
import io.future.laboratories.anilistbingo.ui.theme.AniListBingoTheme

public class MainActivity : ComponentActivity() {
    private val preferences: SharedPreferences by lazy {
        getSharedPreferences(
            PREFERENCE_BASE_KEY,
            MODE_PRIVATE,
        )
    }

    private val runtimeData: SnapshotStateList<BingoData> by lazy { loadAll() }
    private val authorization
        get() = "" +
                "${preferences.getString(PREFERENCE_ACCESS_TYPE, null)} " +
                "${preferences.getString(PREFERENCE_ACCESS_TOKEN, null)}" +
                ""

    private var currentPage: Page by mutableStateOf(Page.OVERVIEW)
    private var isLoggedIn: Boolean by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
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
                json = AniListBody(API.aniListUserQuery, emptyMap())
            ).enqueue { _, response ->
                Log.d("myTag", response.body().toString())
                preferences.edit {
                    putLong(PREFERENCE_ACCESS_USER_ID, response.body()?.data?.viewer?.id ?: -1L)
                }

                api.postAniList(
                    authorization = authorization,
                    json = AniListBody(
                        query = API.aniListListQuery,
                        variables = mapOf("userId" to preferences.getLong(PREFERENCE_ACCESS_USER_ID, -1L))
                    ),
                ).enqueue { _, response ->
                    Log.d("myTag", response.body().toString())
                }
            }
        }

        validateKey()

        setContent {
            AniListBingoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    ) {
                        when (currentPage) {
                            is Page.OVERVIEW -> OverviewPage(
                                context = this@MainActivity,
                                preferences = preferences,
                                data = runtimeData,
                                isLoggedIn = isLoggedIn,
                                onLogout = { isLoggedIn = false },
                                onEdit = { data ->
                                    currentPage = Page.EDITOR(bingoData = data)
                                },
                                onDelete = { data ->
                                    deleteSingle(data.id)
                                    runtimeData.remove(data)
                                }
                            ) { bingoData ->
                                currentPage = Page.BINGO(bingoData)
                            }
                            is Page.BINGO -> BingoPage(bingoData = (currentPage as Page.BINGO).bingoData)
                            is Page.EDITOR -> {
                                EditorPage(
                                    preferences = preferences,
                                    bingoData = (currentPage as Page.EDITOR).bingoData,
                                ) { bingoData ->
                                    save(bingoData)
                                    runtimeData.add(bingoData)
                                    currentPage = Page.OVERVIEW
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
                                Image(
                                    painter = painterResource(id = R.drawable.checked),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize(0.8f)
                                        .rotate(45f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun validateKey() {
        isLoggedIn =
            System.currentTimeMillis() <= preferences.getLong(PREFERENCE_ACCESS_EXPIRED, -1L)
        if (!isLoggedIn) {
            preferences.logout()
        }
    }

    private sealed class Page {
        data object OVERVIEW : Page()

        class EDITOR(var bingoData: BingoData? = null) : Page()

        class BINGO(var bingoData: BingoData) : Page()
    }

    private companion object {
        private const val PREFERENCE_BASE_KEY = "BINGO_PREFERENCE_KEY"
    }
}