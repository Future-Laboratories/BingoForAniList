package io.future.laboratories.anilistbingo

import android.content.Context
import android.content.SharedPreferences
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

//region Strings

internal fun String.colon() = "$this:"

//endregion
