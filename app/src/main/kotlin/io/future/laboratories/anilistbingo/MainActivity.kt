package io.future.laboratories.anilistbingo

import android.content.SharedPreferences
import android.os.Bundle
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
import io.future.laboratories.anilistbingo.data.BingoData
import io.future.laboratories.anilistbingo.pages.BingoPage
import io.future.laboratories.anilistbingo.pages.EditorPage
import io.future.laboratories.anilistbingo.pages.OverviewPage
import io.future.laboratories.anilistbingo.ui.theme.AniListBingoTheme

public class MainActivity : ComponentActivity() {
    private val preferences: SharedPreferences by lazy {
        getSharedPreferences(
            PREFERENCE_BASE_KEY,
            MODE_PRIVATE
        )
    }
    private val runtimeData: SnapshotStateList<BingoData> by lazy { loadAll() }

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
                                onLogin = { isLoggedIn = true },
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
        isLoggedIn = System.currentTimeMillis() <= preferences.getLong(PREFERENCE_ACCESS_EXPIRED, 0L)
        if(!isLoggedIn) {
            preferences.logout()
        }
    }

    private sealed class Page {
        data object OVERVIEW : Page()

        class EDITOR(var bingoData: BingoData? = null) : Page()

        class BINGO(var bingoData: BingoData) : Page()
    }

    public companion object {
        private const val PREFERENCE_BASE_KEY = "BINGO_PREFERENCE_KEY"
        public const val PREFERENCE_ACCESS_TOKEN: String = "${PREFERENCE_BASE_KEY}_TOKEN"
        public const val PREFERENCE_ACCESS_TYPE: String= "${PREFERENCE_BASE_KEY}_TYPE"
        public const val PREFERENCE_ACCESS_EXPIRED: String = "${PREFERENCE_BASE_KEY}_EXPIRED"
    }
}