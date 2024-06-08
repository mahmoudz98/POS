package com.casecode.pos.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.casecode.pos.BuildConfig
import com.casecode.pos.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ComposeFileProvider : FileProvider(
    R.xml.file_paths
){
    companion object {

        fun getImageUri(context: Context): Uri {

            val directory = File(context.cacheDir, "images")
            if (!directory.exists()) directory.mkdirs()
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(Date())

            val file = File.createTempFile(
                "image_${timeStamp}", ".jpg", directory
            )

            val authority = BuildConfig.APPLICATION_ID + ".fileprovider"

            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }

}