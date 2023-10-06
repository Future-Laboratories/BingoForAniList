package io.future.laboratories.anilistbingo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.squareup.moshi.Moshi
import io.future.laboratories.anilistbingo.Companion.STORAGE_PATH
import io.future.laboratories.anilistbingo.data.BingoData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.prefs.Preferences

//region permissions
//fun getPermission(
//    context: Context,
//    activity: Activity,
//    permission: String,
//    onSuccess: () -> Unit = {},
//    onFailure: () -> Unit = {
//        ActivityCompat.requestPermissions(
//            activity,
//            arrayOf(permission),
//            MY_PERMISSIONS_REQUESTS.getValue(permission)
//        )
//    },
//) {
//    if (ContextCompat.checkSelfPermission(
//            context,
//            permission
//        ) == PackageManager.PERMISSION_GRANTED
//    ) {
//        onSuccess()
//    } else {
//        onFailure()
//    }
//}
//
//internal val MY_PERMISSIONS_REQUESTS = mapOf<String, Int>(
//    Manifest.permission.READ_EXTERNAL_STORAGE to 100,
//    Manifest.permission.WRITE_EXTERNAL_STORAGE to 101,
//)
//endregion

//region save & load

internal val BUILDER
    get() = Moshi.Builder().build()

internal val ADAPTER = BUILDER.adapter(BingoData::class.java)

public fun Context.save(data: BingoData) {
    val file = File(filesDir, STORAGE_PATH("${data.id}"))
    if (!file.exists()) {
        file.parentFile?.mkdir()
    }
    val fileWriter = FileWriter(file)
    BufferedWriter(fileWriter).use {
        it.write(ADAPTER.toJson(data).toString())
    }
}

public fun Context.loadAll(): SnapshotStateList<BingoData> {
    val bingoDataList = SnapshotStateList<BingoData>()
    val file = File(filesDir, STORAGE_PATH())

    file.walkTopDown().forEach {
        if (!it.isDirectory) {
            bingoDataList.add(loadSingle(it.name.toInt()) ?: return@forEach)
        }
    }

    return bingoDataList
}

public fun Context.loadSingle(id: Int): BingoData? {
    var data: BingoData? = null

    val file = File(filesDir, STORAGE_PATH("$id"))
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

public fun Context.deleteSingle(id: Int) {
    val file = File(filesDir, STORAGE_PATH("$id"))
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
public fun SharedPreferences.logout() {
    edit {
        putString(MainActivity.PREFERENCE_ACCESS_TOKEN, null)
        putString(MainActivity.PREFERENCE_ACCESS_TYPE, null)
        putLong(MainActivity.PREFERENCE_ACCESS_EXPIRED, 0)
    }
}
//endregion
