package com.casecode.domain.usecase

import com.casecode.domain.repository.FirestoreRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddDocuments @Inject constructor(private val firestoreRepository: FirestoreRepository) {
    suspend operator fun invoke(collection: String, data: Any) =
        firestoreRepository.addDocuments(collection, data)
}