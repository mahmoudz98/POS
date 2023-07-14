package com.casecode.data.repository

import com.casecode.domain.repository.FirestoreRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class FirestoreRepositoryImpl(private val firestore: FirebaseFirestore) : FirestoreRepository {
    override suspend fun getDocuments(collection: String): Task<QuerySnapshot> =
        firestore.collection(collection).get()

    override suspend fun setDocuments(collection: String, data: Any): Task<DocumentReference> {
        val collectionReference = firestore.collection(collection)
        return collectionReference.add(data)
    }
}