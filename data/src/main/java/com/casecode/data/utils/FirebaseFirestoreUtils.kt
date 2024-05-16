package com.casecode.data.utils

import com.casecode.domain.utils.USERS_COLLECTION_PATH
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun <T> Query.snapshotFlow(): Flow<QuerySnapshot> = callbackFlow {
    val listener = addSnapshotListener { value, error ->
        if (error != null) {
            close(error)
            return@addSnapshotListener
        }
        if (value != null) {
            trySendBlocking(value)
        }
    }
    awaitClose { listener.remove() }
}

fun <T> Query.documentChangesFlow(): Flow<List<DocumentChange>> = callbackFlow {
    val listener = addSnapshotListener { value, error ->
        if (error != null) {
            close(error)
            return@addSnapshotListener
        }
        if (value != null) {
            trySendBlocking(value.documentChanges)
        }
    }
    awaitClose { listener.remove() }
}

fun FirebaseFirestore.getDocumentFromUser(uid: String, collection: String): DocumentReference {
    return this.collection(USERS_COLLECTION_PATH).document(uid).collection(collection).document()
}

fun FirebaseFirestore.getDocumentFromUser(
    uid: String,
    collection: String,
    nameDocument: String,
): DocumentReference = this.collection(USERS_COLLECTION_PATH).document(uid).collection(collection)
    .document(nameDocument)


fun FirebaseFirestore.getCollectionRefFromUser(
    uid: String,
    nameCollection: String,
): CollectionReference =
    this.collection(USERS_COLLECTION_PATH).document(uid).collection(nameCollection)