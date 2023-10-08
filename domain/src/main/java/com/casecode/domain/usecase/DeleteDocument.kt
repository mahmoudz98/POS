package com.casecode.domain.usecase

import com.casecode.domain.repository.FirestoreRepository

class DeleteDocument(private val firestoreRepository: FirestoreRepository) {
    suspend operator fun invoke(
        collectionPath: String,
        documentId: String
    ) = firestoreRepository.deleteDocument(collectionPath, documentId)
}