package com.casecode.domain.usecase

import com.casecode.domain.repository.FirestoreRepository

class SetDocuments(private val firestoreRepository: FirestoreRepository) {
    suspend operator fun invoke(collection: String, data: Any) =
        firestoreRepository.setDocuments(collection, data)
}