package io.future.laboratories.anilistbingo

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.core.content.edit
import com.squareup.moshi.Moshi
import io.future.laboratories.anilistbingo.Companion.PREFERENCE_ACCESS_EXPIRED
import io.future.laboratories.anilistbingo.Companion.PREFERENCE_ACCESS_TOKEN
import io.future.laboratories.anilistbingo.Companion.PREFERENCE_ACCESS_TYPE
import io.future.laboratories.anilistbingo.Companion.PREFERENCE_ACCESS_USER_ID
import io.future.laboratories.anilistbingo.Companion.storagePath
import io.future.laboratories.anilistbingo.data.BingoData
import io.future.laboratories.anilistbingo.data.api.API
import io.future.laboratories.anilistbingo.data.api.DataHolder
import io.future.laboratories.anilistbingo.data.api.DataHolderCall
import io.future.laboratories.anilistbingo.data.api.DataHolderCallback
import io.future.laboratories.anilistbingo.data.api.DataHolderResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

//region save & load

internal val BUILDER
    get() = Moshi.Builder().build()

internal val ADAPTER = BUILDER.adapter(BingoData::class.java)

internal fun Context.save(data: BingoData, subPath: String = "${data.id}") {
    val file = File(filesDir, storagePath(subPath))
    if (!file.exists()) {
        file.parentFile?.mkdir()
    }
    val fileWriter = FileWriter(file)
    BufferedWriter(fileWriter).use {
        it.write(ADAPTER.toJson(data).toString())
    }
}

internal fun Context.loadAll(): SnapshotStateList<BingoData> {
    val bingoDataList = SnapshotStateList<BingoData>()
    val file = File(filesDir, storagePath())

    file.walkTopDown().maxDepth(1).forEach {
        if (!it.isDirectory) {
            bingoDataList.add(loadSingle(it.name.toInt()) ?: return@forEach)
        }
    }

    return bingoDataList
}

internal fun Context.loadSingle(bingoId: Int): BingoData? = loadSingle("$bingoId")

internal fun Context.loadSingle(subPath: String): BingoData? {
    var data: BingoData? = null

    val file = File(filesDir, storagePath(subPath))
    if (file.exists()) {
        val fileReader = FileReader(file)
        val stringBuilder = StringBuilder()

        var line: String?

        BufferedReader(fileReader).use {
            line = it.readLine()

            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = it.readLine()
            }
        }

        data = ADAPTER.fromJson(stringBuilder.toString())
    }

    return data
}

internal fun Context.deleteSingle(id: Int) {
    val file = File(filesDir, storagePath("$id"))
    if (file.exists()) {
        file.delete()
    }
}

//endregion

//region Colors

internal val textColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.White else Color.Black

//endregion

//region logout

internal fun SharedPreferences.logout() {
    edit {
        putString(PREFERENCE_ACCESS_TOKEN, null)
        putString(PREFERENCE_ACCESS_TYPE, null)
        putLong(PREFERENCE_ACCESS_EXPIRED, -1L)
        putLong(PREFERENCE_ACCESS_USER_ID, -1L)
    }
}

//endregion

//region retrofit

private val retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create())
    .baseUrl("https://graphql.anilist.co")
    .build()

internal val api: API = retrofit.create(API::class.java)

internal fun <T> DataHolderCall<T>.enqueue(
    onFailure: ((DataHolderCall<T>, Throwable) -> Unit)? = null,
    onResponse: (call: DataHolderCall<T>, response: DataHolderResponse<T>) -> Unit,
) = this.enqueue(object : DataHolderCallback<T> {
    override fun onResponse(
        call: DataHolderCall<T>,
        response: Response<DataHolder<T>>,
    ) = onResponse(call, response)

    override fun onFailure(
        call: DataHolderCall<T>,
        t: Throwable,
    ) {
        Log.e("DataHolderCall", "$t")

        onFailure?.invoke(call, t)
    }
})

//endregion
