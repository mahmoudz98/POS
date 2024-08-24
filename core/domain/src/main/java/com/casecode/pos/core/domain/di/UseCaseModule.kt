package com.casecode.pos.core.domain.di

import com.casecode.pos.core.domain.repository.BusinessRepository
import com.casecode.pos.core.domain.repository.EmployeesBusinessRepository
import com.casecode.pos.core.domain.repository.InvoiceRepository
import com.casecode.pos.core.domain.repository.ItemImageRepository
import com.casecode.pos.core.domain.repository.ItemRepository
import com.casecode.pos.core.domain.repository.SubscriptionsBusinessRepository
import com.casecode.pos.core.domain.repository.SubscriptionsRepository
import com.casecode.pos.core.domain.usecase.AddBranchBusinessUseCase
import com.casecode.pos.core.domain.usecase.AddEmployeesUseCase
import com.casecode.pos.core.domain.usecase.AddInvoiceUseCase
import com.casecode.pos.core.domain.usecase.AddItemUseCase
import com.casecode.pos.core.domain.usecase.CompleteBusinessUseCase
import com.casecode.pos.core.domain.usecase.DeleteItemUseCase
import com.casecode.pos.core.domain.usecase.GetEmployeesBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetInvoicesUseCase
import com.casecode.pos.core.domain.usecase.GetItemsUseCase
import com.casecode.pos.core.domain.usecase.GetSubscriptionBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetSubscriptionsUseCase
import com.casecode.pos.core.domain.usecase.GetTodayInvoicesUseCase
import com.casecode.pos.core.domain.usecase.ItemImageUseCase
import com.casecode.pos.core.domain.usecase.SetBusinessUseCase
import com.casecode.pos.core.domain.usecase.SetEmployeesBusinessUseCase
import com.casecode.pos.core.domain.usecase.SetSubscriptionBusinessUseCase
import com.casecode.pos.core.domain.usecase.UpdateEmployeesUseCase
import com.casecode.pos.core.domain.usecase.UpdateItemUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideSetBusinessUseCase(businessRepository: BusinessRepository) = SetBusinessUseCase(businessRepository)

    @Provides
    fun provideCompleteBusinessUseCase(businessRepository: BusinessRepository) =
        CompleteBusinessUseCase(businessRepository)

    @Provides
    fun provideAddBranchBusinessUseCase(businessRepository: BusinessRepository) =
        AddBranchBusinessUseCase(businessRepository)

    @Provides
    fun provideSetSubscriptionBusinessUseCase(subscriptionsBusinessRepository: SubscriptionsBusinessRepository) =
        SetSubscriptionBusinessUseCase(subscriptionsBusinessRepository)

    @Provides
    fun provideGetSubscriptionBusinessUseCase(subscriptionsBusinessRepository: SubscriptionsBusinessRepository) =
        GetSubscriptionBusinessUseCase(subscriptionsBusinessRepository)

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
    fun provideGetTodayInvoicesUseCase(invoiceRepository: InvoiceRepository) =
        GetTodayInvoicesUseCase(invoiceRepository)
}