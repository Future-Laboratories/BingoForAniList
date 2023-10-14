package io.future.laboratories.common

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.core.content.edit
import com.squareup.moshi.JsonAdapter
import io.future.laboratories.Companion.BUILDER
import io.future.laboratories.Companion.PREFERENCE_ACCESS_EXPIRED
import io.future.laboratories.Companion.PREFERENCE_ACCESS_TOKEN
import io.future.laboratories.Companion.PREFERENCE_ACCESS_TYPE
import io.future.laboratories.Companion.PREFERENCE_USER_ID
import io.future.laboratories.Companion.TEMP_PATH
import io.future.laboratories.Companion.storagePath
import io.future.laboratories.anilistbingo.data.BingoData
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

//region save & load

public inline fun <reified T> getAdapter(): JsonAdapter<T> = BUILDER.adapter(T::class.java)

public inline fun <reified T> Context.save(data: T, storagePath: String) {
    val file = File(filesDir, storagePath)
    if (!file.exists()) {
        file.parentFile?.mkdir()
    }
    val fileWriter = FileWriter(file)
    BufferedWriter(fileWriter).use {
        it.write(getAdapter<T>().toJson(data).toString())
    }
}

public fun Context.loadAllBingoData(): SnapshotStateList<BingoData> {
    val bingoDataList = SnapshotStateList<BingoData>()
    val file = File(filesDir, storagePath())

    file.walkTopDown().maxDepth(1).forEach {
        if (!it.isDirectory) {
            bingoDataList.add(loadSingleBingoData(it.name.toInt()) ?: return@forEach)
        }
    }

    return bingoDataList
}

private fun Context.loadSingleBingoData(bingoId: Int): BingoData? = loadSingle(
    storagePath = storagePath("$bingoId"),
)

public inline fun <reified T> Context.loadSingle(storagePath: String): T? {
    var data: T? = null

    val file = File(filesDir, storagePath)
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

        data = getAdapter<T>().fromJson(stringBuilder.toString())
    }

    return data
}

public fun Context.deleteSingle(storagePath: String) {
    val file = File(filesDir, storagePath)
    if (file.exists()) {
        file.delete()
    }
}

//endregion

//region Colors

public val textColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.White else Color.Black

//endregion

//region logout

public fun SharedPreferences.logout(context: Context) {
    context.deleteSingle(TEMP_PATH)

    edit {
        putString(PREFERENCE_ACCESS_TOKEN, null)
        putString(PREFERENCE_ACCESS_TYPE, null)
        putLong(PREFERENCE_ACCESS_EXPIRED, -1L)
        putLong(PREFERENCE_USER_ID, -1L)
    }
}

//endregion
