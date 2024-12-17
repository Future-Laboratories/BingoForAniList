package io.future.laboratories.anilistbingo.controller

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.core.content.edit
import io.future.laboratories.Companion.PREFERENCE_ACCESS_EXPIRED
import io.future.laboratories.Companion.PREFERENCE_ACCESS_TOKEN
import io.future.laboratories.Companion.PREFERENCE_ACCESS_TYPE
import io.future.laboratories.Companion.PREFERENCE_USER_ID
import io.future.laboratories.anilistapi.API
import io.future.laboratories.anilistapi.api
import io.future.laboratories.anilistapi.data.DetailedAniListData
import io.future.laboratories.anilistapi.data.MainData
import io.future.laboratories.anilistapi.data.Media
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistapi.data.MediaListStatus
import io.future.laboratories.anilistapi.data.PageQueryParams
import io.future.laboratories.anilistapi.data.ScoreFormat
import io.future.laboratories.anilistapi.data.base.AniListMutationBody
import io.future.laboratories.anilistapi.data.base.AniListQueryBody
import io.future.laboratories.anilistapi.enqueue
import io.future.laboratories.anilistbingo.R

internal class APIController private constructor(
    private val preferences: SharedPreferences,
    private val onNetworkError: (Int) -> Unit,
) {
    private val authorization
        get() = "" +
                "${preferences.getString(PREFERENCE_ACCESS_TYPE, null)} " +
                "${preferences.getString(PREFERENCE_ACCESS_TOKEN, null)}" +
                ""

    /**
     * Creates a Intent to Login to AniList.
     */
    internal fun Context.createLoginIntent() {
        val url = getString(R.string.anilist_url)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(
            intent,
            null,
        )
    }

    internal fun processUriData(
        uri: Uri,
        data: RuntimeData,
    ) {
        // put the received values into the given preferences
        preferences.edit(commit = true) {
            putString(PREFERENCE_ACCESS_TOKEN, uri.getValueOfKey("access_token"))
            putString(PREFERENCE_ACCESS_TYPE, uri.getValueOfKey("token_type"))
            putLong(
                PREFERENCE_ACCESS_EXPIRED,
                System.currentTimeMillis() + (uri.getValueOfKey("expires_in")
                    ?: "0").toLong() * 1000L,
            )
        }

        api.postAniListViewer(
            authorization = authorization,
            json = AniListQueryBody(
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

    /**
     * Takes a value [key] and returns the associated value as a String
     *
     * @param key The key searching for
     * @return String that represents the value under the given [key]
     */
    private fun Uri?.getValueOfKey(key: String): String? {
        return this?.fragment
            ?.substringAfter("${key}=")
            ?.substringBefore("&")
    }

    /**
     * fetch data from AniList
     *
     * @param forced if true a fetch will be forced, default set to false
     * @param onFetchFinished additional logic that will be executed when the async fetch finish
     */
    internal fun RuntimeData.fetchAniList(
        forced: Boolean = false,
        onFetchFinished: (MainData?) -> Unit = {},
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
            json = AniListQueryBody(
                query = API.aniListMainQuery,
                variables = mapOf(
                    "userId" to userId,
                ),
            ),
        ).enqueue(
            onFailure = { _, _ -> dataFetchCompleted = true },
            onResponse = { _, listResponse ->
                val code = listResponse.code()
                if (code == 200) {
                    runtimeAniListData = listResponse.body()?.data?.copy()

                    dataFetchCompleted = true

                    onFetchFinished(runtimeAniListData)
                } else {
                    onNetworkError(code)

                    dataFetchCompleted = true
                }
            },
        )
    }

    /**
     * Fetch new page of data
     *
     * @param variableParameter the new parameters to use
     */
    internal fun RuntimeData.fetchNewPage(
        variableParameter: PageQueryParams,
    ) {
        if (currentQuery != variableParameter) {
            runtimeCustomPages.clear()
            currentQuery = variableParameter.clone()
        }

        if (variableParameter.pageNumber.value in runtimeCustomPages.keys) return

        api.postPageData(
            authorization = authorization,
            json = AniListQueryBody(
                query = API.pageQuery,
                variables = variableParameter.toMap(),
            ),
        ).enqueue(
            onFailure = { _, _ -> },
            onResponse = { _, listResponse ->
                val code = listResponse.code()
                if (code == 200) {
                    runtimeCustomPages[variableParameter.pageNumber.value] =
                        listResponse.body()?.data?.page?.media?.toMutableStateList()
                            ?: return@enqueue
                } else {
                    onNetworkError(code)
                }
            }
        )
    }

    /**
     * Mutates User-preferences
     * @param format ScoreFormat to use
     * @param onCallback What to do with the the response
     */
    @ReadOnlyComposable
    internal fun mutateUser(
        format: ScoreFormat,
        onCallback: (String) -> Unit,
    ) {
        api.postUserData(
            authorization = authorization,
            json = AniListMutationBody(
                query = API.aniListUserMutation,
                variables = mapOf(
                    "format" to format,
                ),
            ),
        ).enqueue { _, response ->
            val code = response.code()
            if (code == 200) {
                // Update preferences
                onCallback(
                    response.body()?.data?.updateUser?.mediaListOptions?.scoreFormat?.value
                        ?: return@enqueue
                )
            } else {
                onNetworkError(code)
            }
        }
    }

    /**
     * Mutate an entry in the user's list
     *
     * @param format ScoreFormat to use
     * @param value the Score
     * @param animeData the entry to mutate
     * @param status the status of the entry
     * @param onCallback What to do with the response
     */
    internal fun mutateEntry(
        format: ScoreFormat,
        value: Float,
        animeData: MediaList,
        status: MediaListStatus,
        onCallback: (Float, MediaListStatus) -> Unit,
    ) {
        api.postMediaListEntryMutation(
            authorization = authorization,
            json = AniListMutationBody(
                query = API.SaveMediaListEntryMutation,
                variables = mapOf(
                    "id" to animeData.id,
                    "scoreRaw" to format.convertTo100(value),
                    "status" to status
                ),
            ),
        ).enqueue { _, response ->
            val errorMsg = response.errorBody()?.string()

            if (errorMsg != null) {
                Log.e("mutateEntry", errorMsg)
            } else {
                val data = response.body()?.data?.saveMediaListEntry ?: return@enqueue
                onCallback(
                    data.score,
                    data.status,
                )
            }
        }
    }

    /**
     * Adding a new entry to the user's list and calling [onSuccess] if the API is returning a 200 back
     *
     *  @param mediaId the id of the media to add
     *  @param onSuccess what to do if the API returns a 200 back
     */
    internal fun RuntimeData.addEntry(
        mediaId: Long,
        onSuccess: () -> Unit,
    ) {
        api.postAddMediaListEntryMutation(
            authorization = authorization,
            json = AniListMutationBody(
                query = API.CreateMediaListEntryMutation,
                variables = mapOf(
                    "mediaId" to mediaId,
                ),
            ),
        ).enqueue { _, response ->
            val code = response.code()
            if (code == 200) {
                onSuccess()
            } else {
                Log.d("addEntry", response.errorBody()?.string() ?: "")
                onNetworkError(code)
            }
        }
    }

    internal fun RuntimeData.fetchDetailedData(
        id : Long,
        onSuccess: () -> Unit,
    ) {
        if(id == this.currentDetailedAniListData?.data?.id) {
            onSuccess()

            return
        }

        api.postDetailedAniListData(
            authorization = authorization,
            json = AniListQueryBody(
                query = API.detailedAnimeQuery,
                variables = mapOf(
                    "id" to id,
                ),
            ),
        ).enqueue { _, response ->
            val code = response.code()
            if (code == 200) {
                this.currentDetailedAniListData = response.body()?.data

                onSuccess()
            } else {
                Log.d("fetchDetailedData", response.errorBody()?.string() ?: "")
                onNetworkError(code)
            }
        }
    }

    /**
     * Validate if [PREFERENCE_ACCESS_EXPIRED] is still valid, if not, try to login again
     */
    internal fun Context.validateKey(): Boolean {
        val isLoggedIn = System.currentTimeMillis() <= preferences.getLong(
            PREFERENCE_ACCESS_EXPIRED,
            -1L,
        )

        if (!isLoggedIn) {
            createLoginIntent()
        }

        return isLoggedIn
    }

    /**
     * convert value to 100-base ScoreFormat
     */
    private fun ScoreFormat.convertTo100(value: Float): Int = kotlin.math.round(
        when (this) {
            ScoreFormat.POINT_100 -> value
            ScoreFormat.POINT_10_DECIMAL -> value * 10f
            ScoreFormat.POINT_10 -> value * 10f
            ScoreFormat.POINT_5 -> value * 20f
            ScoreFormat.POINT_3 -> value * 33.3f
        }
    ).toInt()

    internal data class RuntimeData(
        var dataFetchCompleted: Boolean,
        private val initialRuntimeAniListData: MainData? = null,
    ) {
        var isRuntimeAniListDataDirty: Boolean = false
        var runtimeAniListData: MainData? by mutableStateOf(initialRuntimeAniListData)

        var currentQuery : PageQueryParams? = null
        var runtimeCustomPages = mutableStateMapOf<Int, SnapshotStateList<Media>>()

        var currentDetailedAniListData: DetailedAniListData? by mutableStateOf(null)
    }

    companion object {
        @Volatile
        private var instance: APIController? = null

        internal fun getInstance(
            preferences: SharedPreferences,
            onNetworkError: (Int) -> Unit,
        ): APIController =
            instance ?: synchronized(this) {
                instance ?: APIController(
                    preferences = preferences,
                    onNetworkError = onNetworkError,
                ).also { instance = it }
            }
    }
}