package io.future.laboratories.anilistbingo

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import io.future.laboratories.Companion
import io.future.laboratories.anilistapi.API
import io.future.laboratories.anilistapi.api
import io.future.laboratories.anilistapi.data.AniListBody
import io.future.laboratories.anilistapi.data.MediaListCollectionAndUserData
import io.future.laboratories.anilistapi.enqueue
import io.future.laboratories.common.logout

internal class APIController(internal val preferences: SharedPreferences) {
    private val authorization
        get() = "" +
                "${preferences.getString(Companion.PREFERENCE_ACCESS_TYPE, null)} " +
                "${preferences.getString(Companion.PREFERENCE_ACCESS_TOKEN, null)}" +
                ""

    internal fun Uri?.processFragmentData(data: RuntimeData) = this?.fragment?.let {
        preferences.edit {
            val sub1 = it.substringAfter("access_token=")
            putString(Companion.PREFERENCE_ACCESS_TOKEN, sub1.substringBefore("&"))
            val sub2 = it.substringAfter("&token_type=")
            putString(Companion.PREFERENCE_ACCESS_TYPE, sub2.substringBefore("&"))
            val sub3 = it.substringAfter("&expires_in=").substringBefore("&")
            putLong(
                Companion.PREFERENCE_ACCESS_EXPIRED,
                System.currentTimeMillis() + sub3.toInt() * 1000,
            )
        }

        api.postAniListUser(
            authorization = authorization,
            json = AniListBody(
                API.aniListUserQuery,
                emptyMap(),
            ),
        ).enqueue { _, userResponse ->
            preferences.edit {
                putLong(
                    Companion.PREFERENCE_USER_ID,
                    userResponse.body()?.data?.viewer?.id ?: -1L
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
            Companion.PREFERENCE_USER_ID,
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
            runtimeAniListData = listResponse.body()?.data?.copy()

            dataFetchCompleted = true
        }
    }

    internal fun Context.validateKey(): Boolean {
        val isLoggedIn = System.currentTimeMillis() <= preferences.getLong(
            Companion.PREFERENCE_ACCESS_EXPIRED,
            -1L
        )

        if (!isLoggedIn) {
            preferences.logout(context = this)
        }

        return isLoggedIn
    }

    internal data class RuntimeData(
        var dataFetchCompleted: Boolean,
        private val initialRuntimeAniListData: MediaListCollectionAndUserData? = null,
    ) {
        var runtimeAniListData: MediaListCollectionAndUserData? by mutableStateOf(
            initialRuntimeAniListData
        )
    }
}