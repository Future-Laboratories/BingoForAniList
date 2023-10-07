package io.future.laboratories.anilistbingo.data.api

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

internal typealias DataHolderCall<T> = Call<DataHolder<T>>
internal typealias DataHolderCallback<T> = Callback<DataHolder<T>>
internal typealias DataHolderResponse<T> = Response<DataHolder<T>>

internal interface API {
    @POST("/")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
    )
    public fun postAniListUser(
        @Header("Authorization") authorization: String,
        @Query("response_type") token: String = "token",
        @Body json: AniListBody,
    ): DataHolderCall<ViewerData>

    @POST("/")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
    )
    public fun postAniList(
        @Header("Authorization") authorization: String,
        @Query("response_type") token: String = "token",
        @Body json: AniListBody,
    ): DataHolderCall<MediaListCollectionData>

    public companion object {
        public val aniListUserQuery: String = """
                query {
                  Viewer {
                    id
                  }
                }
            """.trimIndent()

        public val aniListListQuery: String = """
                query(${'$'}userId: Int) {
                  MediaListCollection(userId: ${'$'}userId, type: ANIME, status: CURRENT) {
                    lists {
                      name
                      entries {
                        id
                        media {
                          title {
                            userPreferred
                          }
                        }
                      }
                    }
                  }
                }
            """.trimIndent()
    }
}
