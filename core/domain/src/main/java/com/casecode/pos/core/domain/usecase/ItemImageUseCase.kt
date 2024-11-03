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
package com.casecode.pos.core.domain.usecase

import android.graphics.Bitmap
import com.casecode.pos.core.domain.repository.DeleteImage
import com.casecode.pos.core.domain.repository.ItemImageRepository
import com.casecode.pos.core.domain.repository.ReplaceImage
import com.casecode.pos.core.domain.repository.UploadImage
import com.casecode.pos.core.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case class for image-related operations.
 *
 * @property imageRepository The repository responsible for handling image operations.
 * @constructor Creates an [ItemImageUseCase] with the provided [imageRepository].
 */
class ItemImageUseCase
@Inject
constructor(
    private val imageRepository: ItemImageRepository,
) {
    /**
     * Uploads an image represented by [bitmap] with the given [imageName].
     *
     * @param bitmap The bitmap image to be uploaded.
     * @param imageName The name to be assigned to the uploaded image.
     * @return A [UploadImage] resource containing the URL of the uploaded image.
     */
    fun uploadImage(bitmap: Bitmap?, imageName: String?) = flow {
        emit(Resource.Companion.loading())
        emit(
            if (bitmap != null && imageName != null) {
                imageRepository.uploadImage(bitmap, imageName)
            } else {
                Resource.Companion.empty()
            },
        )
    }

    fun replaceOrUploadImage(
        bitmap: Bitmap?,
        imageUrl: String?,
        imageName: String?,
    ): Flow<Resource<String>> = if (imageUrl.isNullOrEmpty()) {
        uploadImage(bitmap, imageName)
    } else {
        replaceImage(bitmap, imageUrl)
    }

    /**
     * Replaces an existing image with the image represented by [bitmap].
     *
     * @param bitmap The bitmap image to replace the existing image.
     * @param imageUrl The URL of the existing image to be replaced.
     * @return A [ReplaceImage] resource containing the URL of the replaced image.
     */
    private fun replaceImage(bitmap: Bitmap?, imageUrl: String?) = flow {
        emit(Resource.Companion.loading())
        emit(
            if (bitmap != null && imageUrl != null) {
                imageRepository.replaceImage(bitmap, imageUrl)
            } else {
                Resource.Companion.empty()
            },
        )
    }

    /**
     * Deletes the image associated with the given [imageUrl].
     *
     * @param imageUrl The URL of the image to be deleted.
     * @return A [DeleteImage] resource indicating the success or failure of the deletion operation.
     */
    suspend fun deleteImage(imageUrl: String?) = flow {
        emit(Resource.Companion.loading())
        emit(
            if (imageUrl.isNullOrEmpty()) {
                Resource.Companion.empty()
            } else {
                imageRepository.deleteImage(imageUrl)
            },
        )
    }
}