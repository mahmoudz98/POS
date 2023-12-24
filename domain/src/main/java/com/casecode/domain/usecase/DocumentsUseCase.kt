package com.casecode.domain.usecase

import com.casecode.domain.repository.FirestoreRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Mahmoud Abdalhafeez on 12/9/2023
 */

@Singleton
class AddDocuments @Inject constructor(private val firestoreRepository: FirestoreRepository) {
   suspend operator fun invoke(collection: String, data: Any) =
      firestoreRepository.addDocuments(collection, data)
}

class  DeleteDocument @Inject constructor(private val firestoreRepository: FirestoreRepository) {
   suspend operator fun invoke(
        collectionPath: String,
        documentId: String
                              ) = firestoreRepository.deleteDocument(collectionPath, documentId)
}

@Singleton
class GetDocuments @Inject constructor(private val firestoreRepository: FirestoreRepository) {
   
   suspend operator fun invoke(collection: String) = firestoreRepository.getDocuments(collection)
   
   suspend operator fun invoke(collection: String, documentId: String, subCollection: String) =
      firestoreRepository.getDocuments(collection, documentId, subCollection)
   
}

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