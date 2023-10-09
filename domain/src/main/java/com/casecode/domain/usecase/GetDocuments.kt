package com.casecode.domain.usecase

import com.casecode.domain.repository.FirestoreRepository

class GetDocuments(private val firestoreRepository: FirestoreRepository) {

    suspend operator fun invoke(collection: String) = firestoreRepository.getDocuments(collection)

    suspend operator fun invoke(collection: String, documentId: String, subCollection: String) =
        firestoreRepository.getDocuments(collection, documentId, subCollection)

}