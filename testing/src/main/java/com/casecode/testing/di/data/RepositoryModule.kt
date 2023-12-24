package com.casecode.testing.di.data

import com.casecode.di.data.RepositoryModule
import com.casecode.domain.repository.BusinessRepository
import com.casecode.domain.repository.EmployeesBusinessRepository
import com.casecode.domain.repository.StoreRepository
import com.casecode.domain.repository.SubscriptionsBusinessRepository
import com.casecode.domain.repository.SubscriptionsRepository
import com.casecode.testing.repository.TestBusinessRepository
import com.casecode.testing.repository.TestEmployeesBusinessRepository
import com.casecode.testing.repository.TestStoreRepository
import com.casecode.testing.repository.TestSubscriptionsBusinessRepository
import com.casecode.testing.repository.TestSubscriptionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
   components = [SingletonComponent::class],
   replaces = [RepositoryModule::class],
              )
interface TestRepositoryModule
{
   
   @Binds
   fun bindTestBusinessRepo(testBusinessRepository: TestBusinessRepository): BusinessRepository
   
   @Binds
   fun bindTestEmployeesBusinessRepo(testEmployeesBusinessRepository: TestEmployeesBusinessRepository): EmployeesBusinessRepository
   
   @Binds
   fun bindTestStoreRepo(testStoreRepository: TestStoreRepository): StoreRepository
   
   @Binds
   fun bindTestSubscriptionsBusinessRepo(testSubscriptionsBusinessRepository: TestSubscriptionsBusinessRepository): SubscriptionsBusinessRepository
   
   @Binds
   fun bindTestSubscriptionsRepo(testSubscriptionsRepository: TestSubscriptionsRepository): SubscriptionsRepository
   
   
}