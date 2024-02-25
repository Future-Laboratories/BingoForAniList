package io.future.laboratories.anilistbingo

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
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


internal fun Context.deleteAllBingoData(bingoId: Int) {
    val file = File(filesDir, "")
    val id = bingoId.toString()

    file.walkTopDown().maxDepth(2).forEach {
        if (!it.isDirectory && it.name == id) {
            it.delete()
        }
    }
}

/**
 * deletes file under given Path
 * @param storagePath subPath of App file directory
 * @return Status if the file got either deleted, doesn't exist or couldn't get deleted
 */
internal fun Context.deleteSingle(storagePath: String): Status {
    val file = File(filesDir, storagePath)
    return if (file.exists()) {
        if (file.delete()) {
            Status.Success
        } else {
            Status.PermissionDenied
        }
    } else {
        Status.FileNotExist
    }
}

internal enum class Status {
    Success,
    FileNotExist,
    PermissionDenied,
}

//endregion

//region logout

internal fun SharedPreferences.logout(context: Context) {
    context.deleteSingle(Companion.TEMP_PATH)

    edit {
        putString(Companion.PREFERENCE_ACCESS_TOKEN, null)
        putString(Companion.PREFERENCE_ACCESS_TYPE, null)
        putLong(Companion.PREFERENCE_ACCESS_EXPIRED, -1L)
        putLong(Companion.PREFERENCE_USER_ID, -1L)
    }
}

//endregion

//region Activity

internal fun Activity.errorCodeHandle(code: Int) = defaultToast(
    message = when (code) {
        429 -> getString(R.string.error_429)
        500 -> getString(R.string.error_500)
        else -> getString(R.string.error_unknown_d, code)
    }
)

internal fun Activity.defaultToast(message: String) = Toast.makeText(
    this,
    message,
    Toast.LENGTH_LONG,
).show()

//endregion