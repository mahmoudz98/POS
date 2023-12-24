package com.casecode.di.data

import com.casecode.data.repository.BusinessRepositoryImpl
import com.casecode.data.repository.EmployeesBusinessRepositoryImpl
import com.casecode.data.repository.FirestoreRepositoryImpl
import com.casecode.data.repository.SubscriptionsRepositoryImpl
import com.casecode.data.repository.StoreRepositoryImpl
import com.casecode.data.repository.SubscriptionsBusinessRepositoryImpl
import com.casecode.domain.repository.BusinessRepository
import com.casecode.domain.repository.EmployeesBusinessRepository
import com.casecode.domain.repository.FirestoreRepository
import com.casecode.domain.repository.SubscriptionsRepository
import com.casecode.domain.repository.StoreRepository
import com.casecode.domain.repository.SubscriptionsBusinessRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
 interface RepositoryModule
{
   
   @Binds
   fun bindBusinessRepo(businessRepositoryImpl: BusinessRepositoryImpl): BusinessRepository
    @Binds
   fun bindEmployeesBusinessRepo(employeesBusinessRepositoryImpl: EmployeesBusinessRepositoryImpl): EmployeesBusinessRepository
   @Binds
   fun bindFirestoreRepo(firestoreRepositoryImpl: FirestoreRepositoryImpl): FirestoreRepository
   @Binds
   fun bindStoreRepo(storeRepositoryImpl: StoreRepositoryImpl): StoreRepository
   @Binds
   fun bindSubscriptionsBusinessRepo(subscriptionsBusinessRepositoryImpl: SubscriptionsBusinessRepositoryImpl): SubscriptionsBusinessRepository
   
   @Binds
   fun bindSubscriptionsRepo(subscriptionsRepositoryImpl: SubscriptionsRepositoryImpl): SubscriptionsRepository



}