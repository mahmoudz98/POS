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
import com.casecode.pos.core.domain.repository.AuthRepository
import com.casecode.pos.core.domain.repository.BusinessRepository
import com.casecode.pos.core.domain.repository.EmployeesBusinessRepository
import com.casecode.pos.core.domain.repository.InvoiceRepository
import com.casecode.pos.core.domain.repository.ItemImageRepository
import com.casecode.pos.core.domain.repository.ItemRepository
import com.casecode.pos.core.domain.repository.SubscriptionsBusinessRepository
import com.casecode.pos.core.domain.repository.SubscriptionsRepository
import com.casecode.pos.core.testing.repository.TestAccountRepository
import com.casecode.pos.core.testing.repository.TestAuthRepository
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
    fun bindTestAccountRepository(testBusinessRepository: TestAccountRepository): AccountRepository

    @Binds
    fun bindTestAuthRepository(authService: TestAuthRepository): AuthRepository

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