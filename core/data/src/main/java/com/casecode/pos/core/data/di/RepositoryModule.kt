package com.casecode.pos.core.data.di

import com.casecode.pos.core.data.repository.BusinessRepositoryImpl
import com.casecode.pos.core.data.repository.EmployeesBusinessRepositoryImpl
import com.casecode.pos.core.data.repository.InvoiceRepositoryImpl
import com.casecode.pos.core.data.repository.ItemImageRepositoryImpl
import com.casecode.pos.core.data.repository.ItemRepositoryImpl
import com.casecode.pos.core.data.repository.SubscriptionsBusinessRepositoryImpl
import com.casecode.pos.core.data.repository.SubscriptionsRepositoryImpl
import com.casecode.pos.core.domain.repository.BusinessRepository
import com.casecode.pos.core.domain.repository.EmployeesBusinessRepository
import com.casecode.pos.core.domain.repository.InvoiceRepository
import com.casecode.pos.core.domain.repository.ItemImageRepository
import com.casecode.pos.core.domain.repository.ItemRepository
import com.casecode.pos.core.domain.repository.SubscriptionsBusinessRepository
import com.casecode.pos.core.domain.repository.SubscriptionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    internal abstract fun bindBusinessRepo(businessRepositoryImpl: BusinessRepositoryImpl): BusinessRepository

    @Binds
    internal abstract fun bindEmployeesBusinessRepo(employeesBusinessRepositoryImpl: EmployeesBusinessRepositoryImpl): EmployeesBusinessRepository

    @Binds
    internal abstract fun bindSubscriptionsBusinessRepo(subscriptionsBusinessRepositoryImpl: SubscriptionsBusinessRepositoryImpl): SubscriptionsBusinessRepository

    @Binds
    internal abstract fun bindSubscriptionsRepo(subscriptionsRepositoryImpl: SubscriptionsRepositoryImpl): SubscriptionsRepository

    @Binds
    internal abstract fun bindItemRepo(itemRepositoryImpl: ItemRepositoryImpl): ItemRepository

    @Binds
    internal abstract fun bindImageRepo(itemImageRepositoryImpl: ItemImageRepositoryImpl): ItemImageRepository

    @Binds
    internal abstract fun bindInvoiceRepo(invoiceRepositoryImpl: InvoiceRepositoryImpl): InvoiceRepository


}