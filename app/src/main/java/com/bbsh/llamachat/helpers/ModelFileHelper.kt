package com.bbsh.llamachat.helpers

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class ModelFileHelper(
    private val context: Context,
    private val onModelReady: (modelPath: String) -> Unit
) {
    lateinit var launcher: ActivityResultLauncher<Array<String>>

    fun registerPicker(activity: AppCompatActivity) {
        launcher = activity.registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                val fileName = getFileName(uri) ?: "model.gguf"
                val copiedPath = copyModelToInternal(uri, fileName)
                onModelReady(copiedPath)
            }
        }
    }

    fun launchPicker() {
        launcher.launch(arrayOf("*/*")) // You can narrow this down if needed
    }

    private fun getFileName(uri: Uri): String? {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                return cursor.getString(nameIndex)
            }
        }
        return null
    }

    private fun copyModelToInternal(uri: Uri, fileName: String): String {
        val destFile = File(context.filesDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }
        return destFile.absolutePath
    }
}
