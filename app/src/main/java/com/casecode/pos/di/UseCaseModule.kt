package com.casecode.pos.di

import com.casecode.domain.repository.FirestoreRepository
import com.casecode.domain.usecase.GetDocuments
import com.casecode.domain.usecase.SetDocuments
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
    fun provideSetDocumentsUseCase(firestoreRepository: FirestoreRepository): SetDocuments {
        return SetDocuments(firestoreRepository)
    }
}