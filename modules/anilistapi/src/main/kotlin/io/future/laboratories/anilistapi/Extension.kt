package io.future.laboratories.anilistapi

import android.util.Log
import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private val retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create())
    .baseUrl("https://graphql.anilist.co")
    .build()

public val api: API = retrofit.create(API::class.java)

public fun <T> DataHolderCall<T>.enqueue(
    onFailure: ((DataHolderCall<T>, Throwable) -> Unit)? = null,
    onResponse: (call: DataHolderCall<T>, response: DataHolderResponse<T>) -> Unit,
): Unit = this.enqueue(object : DataHolderCallback<T> {
    override fun onResponse(
        call: DataHolderCall<T>,
        response: DataHolderResponse<T>,
    ) = onResponse(call, response)

    override fun onFailure(
        call: DataHolderCall<T>,
        t: Throwable,
    ) {
        Log.e("DataHolderCall", "$t")

        onFailure?.invoke(call, t)
    }
})

/**
 * Int representing format YYYYMMDD
 *
 * Example1: 2016 is represented as 20160000
 * Example2: May 1976: 19760500
 */
@JvmInline
public value class FuzzyDateInt(public val value: Int)