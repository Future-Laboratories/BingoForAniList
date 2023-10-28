package io.future.laboratories.anilistbingo

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.edit
import com.squareup.moshi.JsonAdapter
import io.future.laboratories.Companion
import io.future.laboratories.common.BingoData
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStreamReader

//region save & load

public inline fun <reified T> getAdapter(): JsonAdapter<T> =
    Companion.BUILDER.adapter(T::class.java)

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
    val file = File(filesDir, Companion.bingoStoragePath())

    file.walkTopDown().maxDepth(1).forEach {
        if (!it.isDirectory) {
            bingoDataList.add(loadSingleBingoData(it.name.toInt()) ?: return@forEach)
        }
    }

    return bingoDataList
}

private fun Context.loadSingleBingoData(bingoId: Int): BingoData? = loadSingle(
    storagePath = Companion.bingoStoragePath("$bingoId"),
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

public inline fun <reified T> Context.loadSingle(uri: Uri): T? {
    var data: T? = null

    contentResolver.openInputStream(uri)?.use { inputStream ->
        val fileReader = InputStreamReader(inputStream)
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

//region logout

public fun SharedPreferences.logout(context: Context) {
    context.deleteSingle(Companion.TEMP_PATH)

    edit {
        putString(Companion.PREFERENCE_ACCESS_TOKEN, null)
        putString(Companion.PREFERENCE_ACCESS_TYPE, null)
        putLong(Companion.PREFERENCE_ACCESS_EXPIRED, -1L)
        putLong(Companion.PREFERENCE_USER_ID, -1L)
    }
}

//endregion