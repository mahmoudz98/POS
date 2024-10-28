/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.feature.item.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class PhotoUriManager(
    private val appContext: Context,
) {
    fun buildNewUri(): Uri {
        val photosDir = File(appContext.cacheDir, PHOTOS_DIR)
        photosDir.mkdirs()
        val photoFile = File(photosDir, generateFilename())
        val authority = "${appContext.packageName}.$FILE_PROVIDER"
        return FileProvider.getUriForFile(appContext, authority, photoFile)
    }

    /**
     * Create a unique file name based on the time the photo is taken
     */
    private fun generateFilename() = "item-${System.currentTimeMillis()}.jpg"

    companion object {
        private const val PHOTOS_DIR = "photos"
        private const val FILE_PROVIDER = "fileprovider"
    }
}