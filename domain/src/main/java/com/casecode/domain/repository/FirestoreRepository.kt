package com.casecode.domain.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot

interface FirestoreRepository {

    suspend fun getDocuments(collection: String): Task<QuerySnapshot>

    suspend fun addDocuments(collection: String, data: Any): Task<DocumentReference>

    suspend fun updateDocument(
        collectionPath: String,
        documentId: String,
        updates: HashMap<String, Any>
    ): Task<Void>

    suspend fun setDocument(
        collectionPath: String,
        documentId: String,
        updates: Any
    ): Task<Void>

    suspend fun deleteDocument(
        collectionPath: String,
        documentId: String
    ): Task<Void>

}