package com.casecode.data.service

import com.google.firebase.firestore.DocumentChange
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