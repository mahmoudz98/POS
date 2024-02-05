package com.casecode.di.domain

import com.casecode.domain.repository.BusinessRepository
import com.casecode.domain.repository.EmployeesBusinessRepository
import com.casecode.domain.repository.SignRepository
import com.casecode.domain.repository.StoreRepository
import com.casecode.domain.repository.SubscriptionsBusinessRepository
import com.casecode.domain.repository.SubscriptionsRepository
import com.casecode.domain.usecase.GetStoreUseCase
import com.casecode.domain.usecase.GetSubscriptionBusinessUseCase
import com.casecode.domain.usecase.GetSubscriptionsUseCase
import com.casecode.domain.usecase.SetBusinessUseCase
import com.casecode.domain.usecase.SetEmployeesBusinessUseCase
import com.casecode.domain.usecase.SetSubscriptionBusinessUseCase
import com.casecode.domain.usecase.SignInUseCase
import com.casecode.domain.usecase.SignOutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule
{
   @Provides
   fun provideSignInUseCase(signInRepository: SignRepository): SignInUseCase{
      return SignInUseCase(signInRepository)
   }
   @Provides
   fun provideSignOutUseCase(signInRepository: SignRepository): SignOutUseCase{
      return SignOutUseCase(signInRepository)
   }
   
   @Provides
   fun provideSetSubscriptionBusinessUseCase(subscriptionsBusinessRepository: SubscriptionsBusinessRepository): SetSubscriptionBusinessUseCase
   {
      return SetSubscriptionBusinessUseCase(subscriptionsBusinessRepository)
   }
   
   @Provides
   fun provideGetSubscriptionBusinessUseCase(subscriptionsBusinessRepository: SubscriptionsBusinessRepository): GetSubscriptionBusinessUseCase
   {
      return GetSubscriptionBusinessUseCase(subscriptionsBusinessRepository)
   }
   
   @Provides
   fun provideSetBusinessUseCase(storeRep: BusinessRepository): SetBusinessUseCase
   {
      return SetBusinessUseCase(storeRep)
   }
   
   @Provides
   fun provideGetSubscriptionsUseCase(subscriptionsRepository: SubscriptionsRepository): GetSubscriptionsUseCase
   {
      return GetSubscriptionsUseCase(subscriptionsRepository)
   }
   
   @Provides
   fun provideSetEmployeesBusinessUseCase(employeesBusRepo: EmployeesBusinessRepository): SetEmployeesBusinessUseCase
   {
      return SetEmployeesBusinessUseCase(employeesBusRepo)
   }
   
   @Provides
   fun provideGetStoreUseCase(storeRep: StoreRepository): GetStoreUseCase
   {
      return GetStoreUseCase(storeRep)
   }

}