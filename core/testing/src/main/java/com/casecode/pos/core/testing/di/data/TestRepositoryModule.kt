package com.casecode.pos.core.testing.di.data

import com.casecode.pos.core.data.di.RepositoryModule
import com.casecode.pos.core.domain.repository.BusinessRepository
import com.casecode.pos.core.domain.repository.EmployeesBusinessRepository
import com.casecode.pos.core.domain.repository.InvoiceRepository
import com.casecode.pos.core.domain.repository.ItemImageRepository
import com.casecode.pos.core.domain.repository.ItemRepository
import com.casecode.pos.core.domain.repository.SubscriptionsBusinessRepository
import com.casecode.pos.core.domain.repository.SubscriptionsRepository
import com.casecode.pos.core.testing.repository.TestBusinessRepository
import com.casecode.pos.core.testing.repository.TestEmployeesBusinessRepository
import com.casecode.pos.core.testing.repository.TestInvoiceRepository
import com.casecode.pos.core.testing.repository.TestItemImageRepository
import com.casecode.pos.core.testing.repository.TestItemRepository
import com.casecode.pos.core.testing.repository.TestSubscriptionsBusinessRepository
import com.casecode.pos.core.testing.repository.TestSubscriptionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class],
)
interface TestRepositoryModule {
    @Singleton
    @Binds
    fun bindTestBusinessRepo(testBusinessRepository: TestBusinessRepository): BusinessRepository

    @Singleton
    @Binds
    fun bindTestEmployeesBusinessRepo(testEmployeesBusinessRepository: TestEmployeesBusinessRepository): EmployeesBusinessRepository

    @Singleton
    @Binds
    fun bindTestSubscriptionsBusinessRepo(
        testSubscriptionsBusinessRepository: TestSubscriptionsBusinessRepository,
    ): SubscriptionsBusinessRepository

    @Singleton
    @Binds
    fun bindTestSubscriptionsRepo(testSubscriptionsRepository: TestSubscriptionsRepository): SubscriptionsRepository

    @Singleton
    @Binds
    fun bindItemRepo(itemRepositoryImpl: TestItemRepository): ItemRepository

    @Singleton
    @Binds
    fun bindImageRepo(testItemImageRepository: TestItemImageRepository): ItemImageRepository

    @Singleton
    @Binds
    fun bindInvoiceRepo(invoiceRepositoryImpl: TestInvoiceRepository): InvoiceRepository
}