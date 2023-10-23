package io.future.laboratories.anilistbingo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import io.future.laboratories.Companion.bingoStoragePath
import io.future.laboratories.common.BingoData
import io.future.laboratories.common.loadSingle
import java.io.File


internal object ShareController {
    internal const val READ_STORAGE_PERMISSION_CODE = 2205

    fun Context.share(bingoData: BingoData) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        val sharedFile = File(filesDir, bingoStoragePath("${bingoData.id}"))
            .copyTo(
                target = File(filesDir, "SHARE_TMP.aniJson"),
                overwrite = true,
            )

        if (sharedFile.exists()) {
            shareIntent.setType("application/json")
            shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(this, getString(R.string.provider_name), sharedFile)
            )

            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...")
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing File...")

            startActivity(Intent.createChooser(shareIntent, "Share File"))
        }
    }

    fun Activity.receive(uri: Uri): BingoData? {
        val readImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        return if (ContextCompat.checkSelfPermission(
                this,
                readImagePermission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadSingle<BingoData>(uri = uri)
        } else {
            requestPermissions(arrayOf(readImagePermission), READ_STORAGE_PERMISSION_CODE)

            null
        }
    }
}