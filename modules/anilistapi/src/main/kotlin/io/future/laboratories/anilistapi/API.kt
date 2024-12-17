package io.future.laboratories.anilistapi

import io.future.laboratories.anilistapi.data.AddMediaListEntry
import io.future.laboratories.anilistapi.data.DetailedAniListData
import io.future.laboratories.anilistapi.data.MainData
import io.future.laboratories.anilistapi.data.PageData
import io.future.laboratories.anilistapi.data.UpdateMediaListEntry
import io.future.laboratories.anilistapi.data.UpdateUserData
import io.future.laboratories.anilistapi.data.ViewerData
import io.future.laboratories.anilistapi.data.base.AniListMutationBody
import io.future.laboratories.anilistapi.data.base.AniListQueryBody
import io.future.laboratories.anilistapi.data.base.DataHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

public typealias DataHolderCall<T> = Call<DataHolder<T>>
public typealias DataHolderCallback<T> = Callback<DataHolder<T>>
public typealias DataHolderResponse<T> = Response<DataHolder<T>>

public interface API {
    @POST("/")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
    )
    public fun postAniListViewer(
        @Header("Authorization") authorization: String,
        @Query("response_type") token: String = "token",
        @Body json: AniListQueryBody,
    ): DataHolderCall<ViewerData>

    @POST("/")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
    )
    public fun postAniList(
        @Header("Authorization") authorization: String,
        @Query("response_type") token: String = "token",
        @Body json: AniListQueryBody,
    ): DataHolderCall<MainData>

    @POST("/")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
    )
    public fun postPageData(
        @Header("Authorization") authorization: String,
        @Query("response_type") token: String = "token",
        @Body json: AniListQueryBody,
    ): DataHolderCall<PageData>

    @POST("/")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
    )
    public fun postDetailedAniListData(
        @Header("Authorization") authorization: String,
        @Query("response_type") token: String = "token",
        @Body json: AniListQueryBody,
    ): DataHolderCall<DetailedAniListData>

    @POST("/")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
    )
    public fun postUserData(
        @Header("Authorization") authorization: String,
        @Query("response_type") token: String = "token",
        @Body json: AniListMutationBody,
    ): DataHolderCall<UpdateUserData>

    @POST("/")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
    )
    public fun postMediaListEntryMutation(
        @Header("Authorization") authorization: String,
        @Query("response_type") token: String = "token",
        @Body json: AniListMutationBody,
    ): DataHolderCall<UpdateMediaListEntry>

    @POST("/")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
    )
    public fun postAddMediaListEntryMutation(
        @Header("Authorization") authorization: String,
        @Query("response_type") token: String = "token",
        @Body json: AniListMutationBody,
    ): DataHolderCall<AddMediaListEntry>

    public companion object {
        public val aniListViewerQuery: String = """
                query {
                    Viewer {
                         id
                    }
                }
            """.trimIndent()

        private val mediaBody: String = """
                    id
                    title {
                       userPreferred
                    }
                    coverImage {
                       large
                    }
                    tags {
                       name
                    }
            """.trimIndent()

        public val aniListMainQuery: String = """
                query(${'$'}userId: Int) {
                    MediaTagCollection {
                        name
                        isAdult
                    }
                    User(id: ${'$'}userId) {
                        avatar {
                            medium
                        }
                        mediaListOptions {
                           scoreFormat
                        }
                    }
                    MediaListCollection(userId: ${'$'}userId, type: ANIME) {
                        lists {
                             name
                             entries {
                                id
                                mediaId
                                score
                                status
                                media {
                                    $mediaBody
                                }
                            }
                       }
                   }
               }
            """.trimIndent()

        public val pageQuery: String = """
            query(
                ${'$'}pageNumber: Int
                ${'$'}format: [MediaFormat]
                ${'$'}season: MediaSeason
                ${'$'}year: Int
                ${'$'}search: String
                ${'$'}sort: [MediaSort]
            ) {
                Page(page: ${'$'}pageNumber, perPage: 50) {
                    media(
                        format_in: ${'$'}format
                        season: ${'$'}season
                        seasonYear: ${'$'}year
                        search: ${'$'}search
                        sort: ${'$'}sort
                        type: ANIME
                    ) {
                        id
                        title {
                            userPreferred
                        }
                        coverImage {
                            large
                        }
                        tags {
                            name
                        }
                    }
                }
            }
        """.trimIndent()

        public val detailedAnimeQuery: String = """
            query(${'$'}id: Int) {
                Media(id: ${'$'}id) {
                    id
                    title {
                        userPreferred
                    }
                    description(asHtml: false)
                    episodes
                    duration
                    genres
                    averageScore
                    meanScore
                    popularity
                    tags {
                        name
                        rank
                        isAdult
                    }
                    bannerImage
                    coverImage {
                        large
                    }
                    startDate {
                        year
                        month
                        day
                    }
                    endDate {
                        year
                        month
                        day
                    }
                }
            }
        """.trimIndent()

        public val aniListUserMutation: String = """
            mutation(${'$'}format: ScoreFormat) {
                UpdateUser(scoreFormat: ${'$'}format) {
                    mediaListOptions {
                        scoreFormat
                    }
                }
            }
        """.trimIndent()

        public val SaveMediaListEntryMutation: String = """
            mutation(
             ${'$'}id: Int,
             ${'$'}scoreRaw: Int, 
             ${'$'}status: MediaListStatus
            ) {
                SaveMediaListEntry(id: ${'$'}id, scoreRaw: ${'$'}scoreRaw, status: ${'$'}status) {
                    score
                    status
                }
            }
        """.trimIndent()

        public val CreateMediaListEntryMutation: String = """
            mutation(
             ${'$'}mediaId: Int,
            ) {
                SaveMediaListEntry(
                    mediaId: ${'$'}mediaId
                    status: PLANNING
                    hiddenFromStatusLists: false,
                ) {
                    score
                    status
                }
            }
        """.trimIndent()
    }
}
