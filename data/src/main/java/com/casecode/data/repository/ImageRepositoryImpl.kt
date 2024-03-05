package com.casecode.data.repository

import android.graphics.Bitmap
import com.casecode.data.utils.AppDispatchers
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.repository.DeleteImage
import com.casecode.domain.repository.ImageRepository
import com.casecode.domain.repository.ReplaceImage
import com.casecode.domain.repository.UploadImage
import com.casecode.domain.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * Implementation of the [ImageRepository] interface that handles image-related operations using Firebase Storage.
 *
 * @property firebaseStorage FirebaseStorage instance for accessing Firebase Storage.
 * @property ioDispatcher CoroutineDispatcher for performing operations in the background.
 */
class ImageRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    @Dispatcher(AppDispatchers.IO) val ioDispatcher: CoroutineDispatcher,
) : ImageRepository {

    private val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    /**
     * Uploads an image to Firebase Storage.
     *
     * @param bitmap Bitmap image to upload.
     * @param imageName Name of the image.
     * @return [UploadImage] representing the result of the upload operation.
     */
    override suspend fun uploadImage(bitmap: Bitmap, imageName: String): UploadImage {
        return withContext(ioDispatcher) {
            try {
                // Get a reference to the Firebase Storage location
                val storageRef =
                    firebaseStorage.getReference("item/images/$currentUserId/$imageName")

                // Convert bitmap to byte array
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                // Upload image to Firebase Storage
                val uploadTask = storageRef.putBytes(data).await()

                // Get download URL
                val downloadUrl = storageRef.downloadUrl.await().toString()

                // Return the download URL
                Resource.Success(downloadUrl)
            } catch (e: Exception) {
                // Return error if upload fails
                Resource.Error("Error uploading image, ${e.message}")
            }
        }
    }

    /**
     * Replaces an existing image in Firebase Storage with a new one.
     *
     * @param bitmap Bitmap image to replace with.
     * @param imageUrl URL of the existing image to replace.
     * @return [ReplaceImage] representing the result of the replacement operation.
     */
    override suspend fun replaceImage(bitmap: Bitmap, imageUrl: String): ReplaceImage {
        return withContext(ioDispatcher) {
            try {
                // Convert bitmap to byte array
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                // Get a reference to the Firebase Storage location using the existing image URL
                val storageRef = firebaseStorage.getReferenceFromUrl(imageUrl)

                // Replace the existing image with the new one
                val uploadTask = storageRef.putBytes(data).await()

                // Get download URL
                val downloadUrl = storageRef.downloadUrl.await().toString()

                // Return the download URL
                Resource.Success(downloadUrl)
            } catch (e: Exception) {
                // Return error if replacement fails
                Resource.Error("Error replacing image: ${e.message}")
            }
        }
    }

    /**
     * Deletes an image from Firebase Storage.
     *
     * @param imageUrl URL of the image to delete.
     * @return [DeleteImage] representing the result of the deletion operation.
     */
    override suspend fun deleteImage(imageUrl: String): DeleteImage {
        return withContext(ioDispatcher) {
            try {
                // Get a reference to the Firebase Storage location using the image URL
                val storageRef = firebaseStorage.getReferenceFromUrl(imageUrl)

                // Delete the image from Firebase Storage
                storageRef.delete().await()

                // Return true if deletion is successful
                Resource.Success("Image deleted successfully")
            } catch (e: Exception) {
                // Return false if deletion fails
                Resource.Error("Image deleted failure, ${e.message}")
            }
        }
    }

}