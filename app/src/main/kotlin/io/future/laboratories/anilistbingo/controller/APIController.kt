package io.future.laboratories.anilistbingo.controller

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import io.future.laboratories.Companion.PREFERENCE_ACCESS_EXPIRED
import io.future.laboratories.Companion.PREFERENCE_ACCESS_TOKEN
import io.future.laboratories.Companion.PREFERENCE_ACCESS_TYPE
import io.future.laboratories.Companion.PREFERENCE_USER_ID
import io.future.laboratories.anilistapi.API
import io.future.laboratories.anilistapi.api
import io.future.laboratories.anilistapi.data.AniListBody
import io.future.laboratories.anilistapi.data.MainData
import io.future.laboratories.anilistapi.enqueue
import io.future.laboratories.anilistbingo.logout

internal class APIController private constructor(private val preferences: SharedPreferences) {
    private val authorization
        get() = "" +
                "${preferences.getString(PREFERENCE_ACCESS_TYPE, null)} " +
                "${preferences.getString(PREFERENCE_ACCESS_TOKEN, null)}" +
                ""

    internal fun Uri?.processFragmentData(data: RuntimeData) = this?.fragment?.let {
        preferences.edit {
            val sub1 = it.substringAfter("access_token=")
            putString(PREFERENCE_ACCESS_TOKEN, sub1.substringBefore("&"))
            val sub2 = it.substringAfter("&token_type=")
            putString(PREFERENCE_ACCESS_TYPE, sub2.substringBefore("&"))
            val sub3 = it.substringAfter("&expires_in=").substringBefore("&")
            putLong(
                PREFERENCE_ACCESS_EXPIRED,
                System.currentTimeMillis() + sub3.toInt() * 1000,
            )
        }

        api.postAniListViewer(
            authorization = authorization,
            json = AniListBody(
                API.aniListViewerQuery,
                emptyMap(),
            ),
        ).enqueue { _, userResponse ->
            preferences.edit {
                putLong(
                    PREFERENCE_USER_ID,
                    userResponse.body()?.data?.viewer?.id ?: -1L,
                )
            }

            data.fetchAniList(forced = true)
        }
    }

    internal fun RuntimeData.fetchAniList(
        forced: Boolean = false,
    ) {
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
                query = API.aniListMainQuery,
                variables = mapOf(
                    "userId" to userId,
                ),
            ),
        ).enqueue(onFailure = { _, _ -> dataFetchCompleted = true }) { _, listResponse ->
            runtimeAniListData = listResponse.body()?.data?.copy()

            dataFetchCompleted = true
        }
    }

    internal fun Context.validateKey(): Boolean {
        val isLoggedIn = System.currentTimeMillis() <= preferences.getLong(
            PREFERENCE_ACCESS_EXPIRED,
            -1L,
        )

        if (!isLoggedIn) {
            preferences.logout(context = this)
        }

        return isLoggedIn
    }

    internal data class RuntimeData(
        var dataFetchCompleted: Boolean,
        private val initialRuntimeAniListData: MainData? = null,
    ) {
        var runtimeAniListData: MainData? by mutableStateOf(
            initialRuntimeAniListData
        )
    }

    companion object {
        @Volatile
        private var instance: APIController? = null

        internal fun getInstance(preferences: SharedPreferences): APIController =
            instance ?: synchronized(this) {
                instance ?: APIController(preferences).also { instance = it }
            }
    }
}