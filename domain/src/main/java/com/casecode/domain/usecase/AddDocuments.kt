package com.casecode.domain.usecase

import com.casecode.domain.repository.FirestoreRepository

class AddDocuments(private val firestoreRepository: FirestoreRepository) {
    suspend operator fun invoke(collection: String, data: Any) =
        firestoreRepository.addDocuments(collection, data)
}