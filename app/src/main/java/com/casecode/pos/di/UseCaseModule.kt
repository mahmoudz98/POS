package com.casecode.pos.di

import com.casecode.domain.repository.FirestoreRepository
import com.casecode.domain.usecase.AddDocuments
import com.casecode.domain.usecase.DeleteDocument
import com.casecode.domain.usecase.GetDocuments
import com.casecode.domain.usecase.UpdateDocument
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetDocumentsUseCase(firestoreRepository: FirestoreRepository): GetDocuments {
        return GetDocuments(firestoreRepository)
    }

    @Provides
    fun provideAddDocumentsUseCase(firestoreRepository: FirestoreRepository): AddDocuments {
        return AddDocuments(firestoreRepository)
    }

    @Provides
    fun provideUpdateDocumentsUseCase(firestoreRepository: FirestoreRepository): UpdateDocument {
        return UpdateDocument(firestoreRepository)
    }

    @Provides
    fun provideDeleteDocumentsUseCase(firestoreRepository: FirestoreRepository): DeleteDocument {
        return DeleteDocument(firestoreRepository)
    }
}