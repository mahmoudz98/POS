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
package com.casecode.pos.core.domain.repository

import android.graphics.Bitmap
import com.casecode.pos.core.domain.utils.Resource

typealias UploadImage = Resource<String>
typealias ReplaceImage = Resource<String>
typealias DeleteImage = Resource<Boolean>

/**
 * An interface defining methods for image operations such as uploading, replacing, and deleting images.
 */
interface ItemImageRepository {
    /**
     * Uploads an image represented by [bitmap] with the given [imageName].
     *
     * @param bitmap The bitmap image to be uploaded.
     * @param imageName The name to be assigned to the uploaded image.
     * @return A [UploadImage] resource containing the URL of the uploaded image.
     */
    suspend fun uploadImage(
        bitmap: Bitmap,
        imageName: String,
    ): UploadImage

    /**
     * Replaces an existing image with the image represented by [bitmap].
     *
     * @param bitmap The bitmap image to replace the existing image.
     * @param imageUrl The URL of the existing image to be replaced.
     * @return A [ReplaceImage] resource containing the URL of the replaced image.
     */
    suspend fun replaceImage(
        bitmap: Bitmap,
        imageUrl: String,
    ): ReplaceImage

    /**
     * Deletes the image associated with the given [imageUrl].
     *
     * @param imageUrl The URL of the image to be deleted.
     * @return A [DeleteImage] resource indicating the success or failure of the deletion operation.
     */
    suspend fun deleteImage(imageUrl: String): DeleteImage
}