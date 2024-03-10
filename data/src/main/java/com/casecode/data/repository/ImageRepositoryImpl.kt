package com.casecode.data.repository

import android.graphics.Bitmap
import com.casecode.data.utils.AppDispatchers
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.repository.ImageRepository
import com.casecode.domain.utils.Resource
import com.casecode.pos.data.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.net.UnknownHostException
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

    override suspend fun uploadImage(bitmap: Bitmap, imageName: String) = flow {
        try {
            // Emit loading state
            emit(Resource.loading())

            // Get a reference to the Firebase Storage location
            val storageRef = firebaseStorage.getReference("item/images/$currentUserId/$imageName")

            // Convert bitmap to byte array
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            // Upload image to Firebase Storage
            val uploadTask = storageRef.putBytes(data)
            uploadTask.await()

            // Get download URL
            val downloadUrl = storageRef.downloadUrl.await().toString()

            // Emit success state with download URL
            emit(Resource.success(downloadUrl))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
            // Emit error state
            val errorMessage = when (e) {
                is UnknownHostException -> R.string.upload_image_failure_network
                else -> R.string.upload_image_failure_generic
            }
            emit(Resource.error(errorMessage))
        }
    }.flowOn(ioDispatcher)

    override suspend fun replaceImage(bitmap: Bitmap, imageUrl: String) = flow {
        try {
            // Emit loading state
            emit(Resource.loading())

            // Convert bitmap to byte array
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            // Get a reference to the Firebase Storage location using the existing image URL
            val storageRef = firebaseStorage.getReferenceFromUrl(imageUrl)

            // Replace image to Firebase Storage
            val uploadTask = storageRef.putBytes(data)
            uploadTask.await()

            // Get download URL
            val downloadUrl = storageRef.downloadUrl.await().toString()

            // Emit success state with download URL
            emit(Resource.success(downloadUrl))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
            // Emit error state
            val errorMessage = when (e) {
                is UnknownHostException -> R.string.replace_image_failure_network
                else -> R.string.replace_image_failure_generic
            }
            emit(Resource.error(errorMessage))
        }
    }.flowOn(ioDispatcher)

    override suspend fun deleteImage(imageUrl: String) = flow {
        try {
            // Emit loading state
            emit(Resource.loading())

            // Get a reference to the Firebase Storage location using the image URL
            val storageRef = firebaseStorage.getReferenceFromUrl(imageUrl)

            // Delete the image from Firebase Storage
            storageRef.delete().await()

            // Emit success state
            emit(Resource.success(R.string.image_deleted_successfully))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
            // Emit error state
            val errorMessage = when (e) {
                is UnknownHostException -> R.string.delete_image_failure_network
                else -> R.string.delete_image_failure_generic
            }
            emit(Resource.error(errorMessage))
        }
    }.flowOn(ioDispatcher)

    companion object {
        private val TAG = ImageRepositoryImpl::class.java.simpleName
    }

}