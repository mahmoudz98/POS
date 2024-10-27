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
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    internal abstract fun bindAccountRepository(accountServiceImpl: AccountRepositoryImpl): AccountRepository

    @Binds
    internal abstract fun bindAuthRepository(authImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    internal abstract fun bindBusinessRepo(businessRepositoryImpl: BusinessRepositoryImpl): BusinessRepository

    @Binds
    internal abstract fun bindEmployeesBusinessRepo(
        employeesBusinessRepositoryImpl: EmployeesBusinessRepositoryImpl,
    ): EmployeesBusinessRepository

    @Binds
    internal abstract fun bindSubscriptionsBusinessRepo(
        subscriptionsBusinessRepositoryImpl: SubscriptionsBusinessRepositoryImpl,
    ): SubscriptionsBusinessRepository

    @Binds
    internal abstract fun bindSubscriptionsRepo(subscriptionsRepositoryImpl: SubscriptionsRepositoryImpl): SubscriptionsRepository

    @Singleton
    @Binds
    internal abstract fun bindItemRepo(itemRepositoryImpl: ItemRepositoryImpl): ItemRepository

    @Binds
    internal abstract fun bindImageRepo(itemImageRepositoryImpl: ItemImageRepositoryImpl): ItemImageRepository

    @Binds
    internal abstract fun bindInvoiceRepo(invoiceRepositoryImpl: InvoiceRepositoryImpl): InvoiceRepository

    @Binds
    internal abstract fun bindPrinterRepo(printerRepositoryImpl: PrinterRepositoryImpl): PrinterRepository
}