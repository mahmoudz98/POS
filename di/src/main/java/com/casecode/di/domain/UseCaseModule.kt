package com.casecode.di.domain

import com.casecode.domain.repository.BusinessRepository
import com.casecode.domain.repository.EmployeesBusinessRepository
import com.casecode.domain.repository.InvoiceRepository
import com.casecode.domain.repository.ItemImageRepository
import com.casecode.domain.repository.ItemRepository
import com.casecode.domain.repository.StoreRepository
import com.casecode.domain.repository.SubscriptionsBusinessRepository
import com.casecode.domain.repository.SubscriptionsRepository
import com.casecode.domain.usecase.AddBranchBusinessUseCase
import com.casecode.domain.usecase.AddEmployeesUseCase
import com.casecode.domain.usecase.AddInvoiceUseCase
import com.casecode.domain.usecase.AddItemUseCase
import com.casecode.domain.usecase.CompleteBusinessUseCase
import com.casecode.domain.usecase.DeleteItemUseCase
import com.casecode.domain.usecase.GetCurrentUserUseCase
import com.casecode.domain.usecase.GetEmployeesBusinessUseCase
import com.casecode.domain.usecase.GetInvoicesUseCase
import com.casecode.domain.usecase.GetItemsUseCase
import com.casecode.domain.usecase.GetStoreUseCase
import com.casecode.domain.usecase.GetSubscriptionBusinessUseCase
import com.casecode.domain.usecase.GetSubscriptionsUseCase
import com.casecode.domain.usecase.ItemImageUseCase
import com.casecode.domain.usecase.SetBusinessUseCase
import com.casecode.domain.usecase.SetEmployeesBusinessUseCase
import com.casecode.domain.usecase.SetSubscriptionBusinessUseCase
import com.casecode.domain.usecase.SignInUseCase
import com.casecode.domain.usecase.SignOutUseCase
import com.casecode.domain.usecase.UpdateEmployeesUseCase
import com.casecode.domain.usecase.UpdateItemUseCase
import com.casecode.service.AccountService
import com.casecode.service.AuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideSignInUseCase(accountService: AccountService) = SignInUseCase(accountService)

    @Provides
    fun provideSignOutUseCase(accountService: AccountService) = SignOutUseCase(accountService)

    @Provides
    fun provideSetBusinessUseCase(businessRepository: BusinessRepository) =
        SetBusinessUseCase(businessRepository)

    @Provides
    fun provideCompleteBusinessUseCase(businessRepository: BusinessRepository) =
        CompleteBusinessUseCase(businessRepository)

    @Provides
    fun provideAddBranchBusinessUseCase(businessRepository: BusinessRepository) =
        AddBranchBusinessUseCase(businessRepository)


    @Provides
    fun provideSetSubscriptionBusinessUseCase(
        subscriptionsBusinessRepository: SubscriptionsBusinessRepository,
    ) = SetSubscriptionBusinessUseCase(subscriptionsBusinessRepository)

    @Provides
    fun provideGetSubscriptionBusinessUseCase(
        subscriptionsBusinessRepository: SubscriptionsBusinessRepository,
    ) = GetSubscriptionBusinessUseCase(subscriptionsBusinessRepository)

    @Provides
    fun provideGetSubscriptionsUseCase(subscriptionsRepository: SubscriptionsRepository) =
        GetSubscriptionsUseCase(subscriptionsRepository)

    @Provides
    fun provideSetEmployeesBusinessUseCase(employeesBusRepo: EmployeesBusinessRepository) =
        SetEmployeesBusinessUseCase(employeesBusRepo)

    @Provides
    fun provideAddEmployeesBusinessUseCase(employeesBusRepo: EmployeesBusinessRepository) =
        AddEmployeesUseCase(employeesBusRepo)

    @Provides
    fun provideUpdateEmployeesBusinessUseCase(employeesBusRepo: EmployeesBusinessRepository) =
        UpdateEmployeesUseCase(employeesBusRepo)

    @Provides
    fun provideGetEmployeesBusinessUseCase(employeesBusRepo: EmployeesBusinessRepository) =
        GetEmployeesBusinessUseCase(employeesBusRepo)

    @Provides
    fun provideGetStoreUseCase(storeRep: StoreRepository) = GetStoreUseCase(storeRep)

    @Provides
    fun provideGetItemsUseCase(itemRepository: ItemRepository) = GetItemsUseCase(itemRepository)

    @Provides
    fun provideAddItemsUseCase(itemRepository: ItemRepository) = AddItemUseCase(itemRepository)

    @Provides
    fun provideUpdateItemUseCase(itemRepository: ItemRepository) = UpdateItemUseCase(itemRepository)

    @Provides
    fun provideDeleteItemUseCase(itemRepository: ItemRepository) = DeleteItemUseCase(itemRepository)

    @Provides
    fun provideImageUseCase(imageRepository: ItemImageRepository) =
        ItemImageUseCase(imageRepository)

    @Provides
    fun provideAddInvoiceUseCase(invoiceRepository: InvoiceRepository) =
        AddInvoiceUseCase(invoiceRepository)

    @Provides
    fun provideGetInvoicesUseCase(invoiceRepository: InvoiceRepository) =
        GetInvoicesUseCase(invoiceRepository)

    @Provides
    fun provideGetCurrentUserUseCase(auth: AuthService) = GetCurrentUserUseCase(auth)
}