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
package com.casecode.pos.core.testing.di.data

import com.casecode.pos.core.data.di.RepositoryModule
import com.casecode.pos.core.domain.repository.AccountRepository
import com.casecode.pos.core.domain.repository.AuthRepositoryO
import com.casecode.pos.core.domain.repository.EmployeesBusinessRepository
import com.casecode.pos.core.domain.repository.InvoiceRepository
import com.casecode.pos.core.domain.repository.ItemImageRepository
import com.casecode.pos.core.domain.repository.ItemRepository
import com.casecode.pos.core.domain.repository.PrinterRepository
import com.casecode.pos.core.domain.repository.SubscriptionsBusinessRepository
import com.casecode.pos.core.domain.repository.SubscriptionsRepository
import com.casecode.pos.core.domain.repository.SupplierInvoiceRepository
import com.casecode.pos.core.domain.repository.SupplierRepository
import com.casecode.pos.core.domain.repository.business.AuthRepository
import com.casecode.pos.core.domain.repository.business.BranchRepository
import com.casecode.pos.core.domain.repository.business.BusinessRepository
import com.casecode.pos.core.domain.repository.business.CurrencyRepository
import com.casecode.pos.core.domain.repository.business.EmployeeRepository
import com.casecode.pos.core.domain.repository.business.SessionRepository
import com.casecode.pos.core.domain.repository.business.SubscriptionRepository
import com.casecode.pos.core.domain.repository.business.TaxRepository
import com.casecode.pos.core.testing.repository.TestAccountRepository
import com.casecode.pos.core.testing.repository.TestAuthRepositoryO
import com.casecode.pos.core.testing.repository.TestBusinessRepository
import com.casecode.pos.core.testing.repository.TestEmployeesBusinessRepository
import com.casecode.pos.core.testing.repository.TestInvoiceRepository
import com.casecode.pos.core.testing.repository.TestItemImageRepository
import com.casecode.pos.core.testing.repository.TestItemRepository
import com.casecode.pos.core.testing.repository.TestPrinterINfoRepo
import com.casecode.pos.core.testing.repository.TestSubscriptionsBusinessRepository
import com.casecode.pos.core.testing.repository.TestSubscriptionsRepository
import com.casecode.pos.core.testing.repository.TestSupplierInvoicesRepository
import com.casecode.pos.core.testing.repository.TestSupplierRepository
import com.casecode.pos.core.testing.repository.business.TestAuthRepository
import com.casecode.pos.core.testing.repository.business.TestBranchRepository
import com.casecode.pos.core.testing.repository.business.TestCurrencyRepository
import com.casecode.pos.core.testing.repository.business.TestEmployeeRepository
import com.casecode.pos.core.testing.repository.business.TestSessionRepository
import com.casecode.pos.core.testing.repository.business.TestSubscriptionRepository
import com.casecode.pos.core.testing.repository.business.TestTaxRepository
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
    @Binds
    @Singleton
    fun bindTestAuthRepository(impl: TestAuthRepository): AuthRepository

    @Binds
    @Singleton
    fun bindTestBusinessRepository(impl: com.casecode.pos.core.testing.repository.business.TestBusinessRepository): BusinessRepository

    @Binds
    @Singleton
    fun bindTestBranchRepository(impl: TestBranchRepository): BranchRepository

    @Binds
    @Singleton
    fun bindTestEmployeeRepository(impl: TestEmployeeRepository): EmployeeRepository

    @Binds
    @Singleton
    fun bindTestSessionRepository(impl: TestSessionRepository): SessionRepository

    @Binds
    @Singleton
    fun bindTestSubscriptionRepository(impl: TestSubscriptionRepository): SubscriptionRepository

    @Binds
    @Singleton
    fun bindTestTaxRepository(impl: TestTaxRepository): TaxRepository

    @Binds
    @Singleton
    fun bindTestCurrencyRepository(impl: TestCurrencyRepository): CurrencyRepository

    // old to refactor
    @Singleton
    @Binds
    fun bindAuthO(itemRepositoryImpl: TestAuthRepositoryO): AuthRepositoryO

    @Singleton
    @Binds
    fun bindEmployeeO(itemRepositoryImpl: TestEmployeesBusinessRepository): EmployeesBusinessRepository

    @Singleton
    @Binds
    fun bindBusinessO(itemRepositoryImpl: TestBusinessRepository): com.casecode.pos.core.domain.repository.BusinessRepository

    @Singleton
    @Binds
    fun bindPrintINfo(itemRepositoryImpl: TestPrinterINfoRepo): PrinterRepository

    @Singleton
    @Binds
    fun bindSubscriptionBusinessO(itemRepositoryImpl: TestSubscriptionsBusinessRepository): SubscriptionsBusinessRepository

    @Singleton
    @Binds
    fun bindSubscriptionO(itemRepositoryImpl: TestSubscriptionsRepository): SubscriptionsRepository

    @Singleton
    @Binds
    fun bindAccountO(impl: TestAccountRepository): AccountRepository

    @Singleton
    @Binds
    fun bindItemRepo(itemRepositoryImpl: TestItemRepository): ItemRepository

    @Singleton
    @Binds
    fun bindImageRepo(testItemImageRepository: TestItemImageRepository): ItemImageRepository

    @Singleton
    @Binds
    fun bindInvoiceRepo(invoiceRepositoryImpl: TestInvoiceRepository): InvoiceRepository

    @Singleton
    @Binds
    fun bindTestSupplierRepo(testSupplierRepository: TestSupplierRepository): SupplierRepository

    @Singleton
    @Binds
    fun bindTestSupplierInvoiceRepo(
        testSupplierInvoiceRepository: TestSupplierInvoicesRepository,
    ): SupplierInvoiceRepository
}