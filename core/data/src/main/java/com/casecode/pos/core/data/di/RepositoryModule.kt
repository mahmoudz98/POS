/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.core.data.di

import com.casecode.pos.core.data.repository.AccountRepositoryImpl
import com.casecode.pos.core.data.repository.AuthRepositoryImpl
import com.casecode.pos.core.data.repository.BusinessRepositoryImpl
import com.casecode.pos.core.data.repository.EmployeesBusinessRepositoryImpl
import com.casecode.pos.core.data.repository.InvoiceRepositoryImpl
import com.casecode.pos.core.data.repository.ItemImageRepositoryImpl
import com.casecode.pos.core.data.repository.ItemRepositoryImpl
import com.casecode.pos.core.data.repository.PrinterRepositoryImpl
import com.casecode.pos.core.data.repository.SubscriptionsBusinessRepositoryImpl
import com.casecode.pos.core.data.repository.SubscriptionsRepositoryImpl
import com.casecode.pos.core.data.repository.SupplierInvoiceRepositoryImpl
import com.casecode.pos.core.data.repository.SupplierRepositoryImpl
import com.casecode.pos.core.domain.repository.AccountRepository
import com.casecode.pos.core.domain.repository.AuthRepository
import com.casecode.pos.core.domain.repository.BusinessRepository
import com.casecode.pos.core.domain.repository.EmployeesBusinessRepository
import com.casecode.pos.core.domain.repository.InvoiceRepository
import com.casecode.pos.core.domain.repository.ItemImageRepository
import com.casecode.pos.core.domain.repository.ItemRepository
import com.casecode.pos.core.domain.repository.PrinterRepository
import com.casecode.pos.core.domain.repository.SubscriptionsBusinessRepository
import com.casecode.pos.core.domain.repository.SubscriptionsRepository
import com.casecode.pos.core.domain.repository.SupplierInvoiceRepository
import com.casecode.pos.core.domain.repository.SupplierRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindBusinessRepo(impl: BusinessRepositoryImpl): BusinessRepository

    @Binds
    abstract fun bindEmployeesBusinessRepo(
        impl: EmployeesBusinessRepositoryImpl,
    ): EmployeesBusinessRepository

    @Binds
    abstract fun bindSubscriptionsBusinessRepo(
        impl: SubscriptionsBusinessRepositoryImpl,
    ): SubscriptionsBusinessRepository

    @Binds
    abstract fun bindSubscriptionsRepo(impl: SubscriptionsRepositoryImpl): SubscriptionsRepository

    @Singleton
    @Binds
    abstract fun bindItemRepo(impl: ItemRepositoryImpl): ItemRepository

    @Binds
    abstract fun bindImageRepo(impl: ItemImageRepositoryImpl): ItemImageRepository

    @Binds
    abstract fun bindInvoiceRepo(impl: InvoiceRepositoryImpl): InvoiceRepository

    @Binds
    abstract fun bindPrinterRepo(impl: PrinterRepositoryImpl): PrinterRepository

    @Binds
    abstract fun bindSupplierRepo(impl: SupplierRepositoryImpl): SupplierRepository

    @Binds
    abstract fun bindSupplierInvoiceRepo(
        impl: SupplierInvoiceRepositoryImpl,
    ): SupplierInvoiceRepository
}