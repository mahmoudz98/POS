package com.casecode.pos.feature.item

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class PhotoUriManager(private val context: Context) {
    fun buildNewUri(): Uri {
        val photosDir = File(context.cacheDir, PHOTOS_DIR)
        photosDir.mkdirs()
        val photoFile = File(photosDir, generateFilename())
        val authority = "${context.packageName}.$FILE_PROVIDER"
        return FileProvider.getUriForFile(context, authority, photoFile)
    }
    /**
     * Create a unique file name based on the time the photo is taken
     */
    private fun generateFilename() = "item-${System.currentTimeMillis()}.jpg"

    companion object {
        private const val PHOTOS_DIR = "images"
        private const val FILE_PROVIDER = "fileprovider"
    }



}