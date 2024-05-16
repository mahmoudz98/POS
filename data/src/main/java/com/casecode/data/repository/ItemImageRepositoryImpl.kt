package com.casecode.data.repository

import android.graphics.Bitmap
import com.casecode.data.utils.AppDispatchers
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.repository.DeleteImage
import com.casecode.domain.repository.ItemImageRepository
import com.casecode.domain.repository.ReplaceImage
import com.casecode.domain.repository.UploadImage
import com.casecode.domain.utils.IMAGES_PATH_FIELD
import com.casecode.domain.utils.ITEM_PATH_FIELD
import com.casecode.domain.utils.Resource
import com.casecode.pos.data.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Implementation of the [ItemImageRepository] interface that handles image-related operations using Firebase Storage.
 *
 * @property firebaseStorage FirebaseStorage instance for accessing Firebase Storage.
 * @property ioDispatcher CoroutineDispatcher for performing operations in the background.
 */
class ItemImageRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    @Dispatcher(AppDispatchers.IO) val ioDispatcher: CoroutineDispatcher,
) : ItemImageRepository {

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
                    firebaseStorage.getReference("$ITEM_PATH_FIELD/$IMAGES_PATH_FIELD/$currentUserId/$imageName")
                val image = compressImage(bitmap)

                suspendCoroutine { continuation ->
                    // Upload image to Firebase Storage
                    storageRef.putBytes(image).addOnSuccessListener {
                        // Get download URL
                        storageRef.downloadUrl.addOnSuccessListener { uri ->

                            val downloadUrl = uri.toString()
                            continuation.resume(Resource.success(downloadUrl))

                        }.addOnFailureListener { downloadUrlFailure ->
                            Timber.e(downloadUrlFailure)
                            continuation.resume(Resource.error(R.string.download_url_failure))
                        }
                    }.addOnFailureListener { uploadFailure ->
                        Timber.e(uploadFailure)
                        continuation.resume(Resource.error(R.string.upload_image_failure))
                    }
                }
            } catch (e: UnknownHostException) {
                Timber.e(e)
                Resource.error(R.string.upload_image_failure_network)
            } catch (e: Exception) {
                Timber.e(e)
                Resource.error(R.string.upload_image_failure)
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
                val data = compressImage(bitmap)

                // Get a reference to the Firebase Storage location using the existing image URL
                val storageRef = firebaseStorage.getReferenceFromUrl(imageUrl)

                suspendCoroutine { continuation ->
                    // Replace image to Firebase Storage
                    storageRef.putBytes(data).addOnSuccessListener {
                        // Get download URL
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()
                            continuation.resume(Resource.success(downloadUrl))
                        }.addOnFailureListener { downloadUrlFailure ->
                            Timber.e(downloadUrlFailure)
                            continuation.resume(Resource.error(R.string.download_url_failure))
                        }
                    }.addOnFailureListener { replaceFailure ->
                        Timber.e(replaceFailure)
                        continuation.resume(Resource.error(R.string.replace_image_failure))
                    }
                }

            } catch (e: UnknownHostException) {
                Resource.error(R.string.replace_image_failure_network)

            } catch (e: Exception) {
                Timber.e(e)
                Resource.error(R.string.replace_image_failure)
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

                Timber.d("deleteImage: $imageUrl")
                // Get a reference to the Firebase Storage location using the image URL
                val storageRef = firebaseStorage.getReferenceFromUrl(imageUrl)

                suspendCoroutine { continuation ->
                    // Delete the image from Firebase Storage
                    storageRef.delete().addOnSuccessListener {
                        continuation.resume(Resource.success(true))
                    }.addOnFailureListener { deleteFailure ->
                        Timber.e(deleteFailure)
                        continuation.resume(Resource.error(R.string.delete_image_failure_generic))
                    }
                }
            } catch (e: UnknownHostException) {
                Resource.error(R.string.delete_image_failure_network)

            } catch (e: Exception) {
                Timber.e(e)
                Resource.error(R.string.delete_image_failure_generic)
            }
        }
    }

    private fun compressImage(bitmap: Bitmap): ByteArray = ByteArrayOutputStream().let {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        it.toByteArray()
    }
}