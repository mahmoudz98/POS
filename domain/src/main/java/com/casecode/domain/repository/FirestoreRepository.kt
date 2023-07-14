package com.casecode.domain.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot

interface FirestoreRepository {
    suspend fun getDocuments(collection: String): Task<QuerySnapshot>
    suspend fun setDocuments(collection: String, data: Any): Task<DocumentReference>
}