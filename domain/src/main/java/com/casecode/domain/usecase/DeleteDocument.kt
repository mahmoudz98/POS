package com.casecode.domain.usecase

import com.casecode.domain.repository.FirestoreRepository
import javax.inject.Inject
import javax.inject.Singleton

class  DeleteDocument @Inject constructor(private val firestoreRepository: FirestoreRepository) {
    suspend operator fun invoke(
        collectionPath: String,
        documentId: String
    ) = firestoreRepository.deleteDocument(collectionPath, documentId)
}