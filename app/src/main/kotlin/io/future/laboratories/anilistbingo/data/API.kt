package io.future.laboratories.anilistbingo.data

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

//TODO
public interface API {
    @POST
    public fun postOauth(
        @Query("client_id") clientId: Int,
        @Query("response_type") token: String = "token",
    ) : Call<Void>
}
