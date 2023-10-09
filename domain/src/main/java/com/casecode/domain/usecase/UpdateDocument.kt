package com.casecode.domain.usecase

import com.casecode.domain.repository.FirestoreRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateDocument @Inject constructor(private val firestoreRepository: FirestoreRepository) {
    suspend operator fun invoke(
        collectionPath: String,
        documentId: String,
        updates: Any
    ) = firestoreRepository.setDocument(collectionPath, documentId, updates)

    suspend fun updateDocument(
        collectionPath: String,
        documentId: String,
        updates: HashMap<String, Any>
    ) = firestoreRepository.updateDocument(collectionPath, documentId, updates)
}