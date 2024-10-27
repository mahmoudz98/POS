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
package com.casecode.pos.core.firebase.services

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenSource
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.SnapshotListenOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

typealias FieldValue = com.google.firebase.firestore.FieldValue
typealias SetOptions = com.google.firebase.firestore.SetOptions

@Singleton
class FirestoreService @Inject constructor(private val firestore: FirebaseFirestore) {
    // TODO: improve minimize cost of use server with use offline cache to get document from
    private val optionsCache by lazy {
        SnapshotListenOptions
            .Builder()
            .setMetadataChanges(MetadataChanges.INCLUDE)
            .setSource(ListenSource.CACHE)
            .build()
    }

    suspend fun getDocument(collection: String, documentId: String): DocumentSnapshot = try {
        firestore.collection(collection).document(documentId).get().await()
    } catch (e: Exception) {
        Timber.e(e, "Failed to get document")
        throw e
    }

    fun getDocumentInChild(
        collectionParent: String,
        documentId: String,
        collectionChild: String,
    ): DocumentReference {
        trace(collectionChild) {
            return firestore.collection(collectionParent).document(documentId)
                .collection(collectionChild).document()
        }
    }

    fun getOrAddDocumentInChild(
        collectionParent: String,
        documentId: String,
        collectionChild: String,
        nameNewDocument: String,
    ): DocumentReference {
        trace(collectionChild) {
            return firestore.collection(collectionParent).document(documentId)
                .collection(collectionChild).document(nameNewDocument)
        }
    }

    fun getCollection(collection: String): CollectionReference = firestore.collection(collection)

    fun getCollectionChild(
        collectionParent: String,
        documentId: String,
        collectionChild: String,
    ): CollectionReference = firestore.collection(collectionParent).document(documentId)
        .collection(collectionChild)

    suspend fun setDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any>,
    ): Void = try {
        firestore.collection(collection).document(documentId).set(data).await()
    } catch (e: Exception) {
        Timber.e(e, "Failed to set document")
        throw e
    }

    fun setDocumentWithTask(
        collection: String,
        documentId: String,
        data: Map<String, Any>,
    ): Task<Void> = try {
        firestore.collection(collection).document(documentId).set(data)
    } catch (e: Exception) {
        Timber.e(e, "Failed to set document")
        throw e
    }

    suspend fun updateDocument(
        collection: String,
        documentId: String,
        updates: Map<String, Any>,
    ): Boolean {
        trace(updates.keys.toString()) {
            return try {
                firestore.collection(collection)
                    .document(documentId)
                    .update(updates).await()
                true
            } catch (e: Exception) {
                Timber.e(e, "Failed to update document$e")
                false
            }
        }
    }

    fun updateDocumentWithTask(
        collection: String,
        documentId: String,
        updates: Map<String, Any>,
    ): Task<Void> {
        trace(updates.keys.toString()) {
            return try {
                firestore.collection(collection).document(documentId).update(updates)
            } catch (e: Exception) {
                Timber.e(e, "Failed to update document$e")
                throw e
            }
        }
    }

    fun listenToDocument(
        collection: String,
        documentId: String,
        callback: (DocumentSnapshot?, FirebaseFirestoreException?) -> Unit,
    ) {
        firestore.collection(collection).document(documentId).firestore.collection(collection)
            .document(documentId).addSnapshotListener { snapshot, error ->
                callback(snapshot, error)
            }
    }

    fun listenToCollection(
        collection: String,
        documentId: String,
    ) = firestore.collection(collection).document(documentId).snapshots()

    fun listenToCollectionChild(
        collection: String,
        documentId: String,
        collectionChild: String,
        condition: Pair<String, Any>,
        sortWithFieldName: String,
    ) = firestore.collection(collection).document(documentId)
        .collection(collectionChild)
        .whereEqualTo(condition.first, condition.second)
        .orderBy(sortWithFieldName)
        .snapshots()

    fun batch() = firestore.batch()
}