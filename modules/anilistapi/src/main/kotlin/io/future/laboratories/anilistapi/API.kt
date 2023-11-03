package io.future.laboratories.anilistapi

import io.future.laboratories.anilistapi.data.AniListBody
import io.future.laboratories.anilistapi.data.DataHolder
import io.future.laboratories.anilistapi.data.MainData
import io.future.laboratories.anilistapi.data.ViewerData
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
    ): DataHolderCall<MainData>

    public companion object {
        public val aniListViewerQuery: String = """
                query {
                  Viewer {
                    id
                  }
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
                  }
                  MediaListCollection(userId: ${'$'}userId, type: ANIME) {
                    lists {
                      name
                      entries {
                        media {
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
                  }
                }
            """.trimIndent()
    }
}
