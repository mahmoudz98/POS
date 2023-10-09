package com.casecode.di.data

import com.casecode.data.repository.BusinessRepositoryImpl
import com.casecode.data.repository.FirestoreRepositoryImpl
import com.casecode.data.repository.SubscriptionsRepositoryImpl
import com.casecode.data.repository.StoreRepositoryImpl
import com.casecode.domain.repository.BusinessRepository
import com.casecode.domain.repository.FirestoreRepository
import com.casecode.domain.repository.SubscriptionsRepository
import com.casecode.domain.repository.StoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
 interface RepositoryModule
{
   
   @Binds
   fun provideBusinessRepo(businessRepositoryImpl: BusinessRepositoryImpl): BusinessRepository
   
   @Binds
   fun provideSubscriptionsRepo(plansRepositoryImpl: SubscriptionsRepositoryImpl): SubscriptionsRepository
   
   @Binds
   fun provideStoreRepo(storeRepositoryImpl: StoreRepositoryImpl): StoreRepository
   
   @Binds
   fun provideFirestoreRepo(firestoreRepositoryImpl: FirestoreRepositoryImpl): FirestoreRepository
}