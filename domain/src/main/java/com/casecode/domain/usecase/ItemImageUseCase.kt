package com.casecode.domain.usecase

import android.graphics.Bitmap
import com.casecode.domain.repository.DeleteImage
import com.casecode.domain.repository.ItemImageRepository
import com.casecode.domain.repository.ReplaceImage
import com.casecode.domain.repository.UploadImage
import com.casecode.domain.utils.Resource
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
@Inject constructor(private val imageRepository: ItemImageRepository) {
    /**
     * Uploads an image represented by [bitmap] with the given [imageName].
     *
     * @param bitmap The bitmap image to be uploaded.
     * @param imageName The name to be assigned to the uploaded image.
     * @return A [UploadImage] resource containing the URL of the uploaded image.
     */
     fun uploadImage(bitmap: Bitmap?,imageName: String?,) = flow {
        emit(UploadImage.loading())
        emit(
            if (bitmap != null && imageName != null) imageRepository.uploadImage(bitmap, imageName)
            else UploadImage.empty(),
        )
    }
    fun replaceOrUploadImage(bitmap: Bitmap?,
                             imageUrl: String?,
                             imageName: String?,): Flow<Resource<String>> {
       return if(imageUrl.isNullOrEmpty()) uploadImage(bitmap,imageName)
        else replaceImage(bitmap, imageUrl)
    }
    /**
     * Replaces an existing image with the image represented by [bitmap].
     *
     * @param bitmap The bitmap image to replace the existing image.
     * @param imageUrl The URL of the existing image to be replaced.
     * @return A [ReplaceImage] resource containing the URL of the replaced image.
     */
    private fun replaceImage(
        bitmap: Bitmap?,
        imageUrl: String?,
    ) = flow {

        emit(ReplaceImage.loading())
        emit( if(bitmap != null && imageUrl != null) imageRepository.replaceImage(bitmap, imageUrl)
        else ReplaceImage.empty())
    }

    /**
     * Deletes the image associated with the given [imageUrl].
     *
     * @param imageUrl The URL of the image to be deleted.
     * @return A [DeleteImage] resource indicating the success or failure of the deletion operation.
     */
    suspend fun deleteImage(imageUrl: String?) = flow {
        emit(DeleteImage.loading())
        emit(
            if (imageUrl.isNullOrEmpty()) {
                DeleteImage.empty()
            } else {
                imageRepository.deleteImage(imageUrl)
            },
        )
    }
}