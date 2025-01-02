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
package com.casecode.pos.core.testing.base

import com.casecode.pos.core.domain.usecase.AddEmployeeUseCase
import com.casecode.pos.core.domain.usecase.AddEmployeesBusinessUseCase
import com.casecode.pos.core.domain.usecase.AddInvoiceUseCase
import com.casecode.pos.core.domain.usecase.AddItemUseCase
import com.casecode.pos.core.domain.usecase.CompleteBusinessUseCase
import com.casecode.pos.core.domain.usecase.DeleteItemUseCase
import com.casecode.pos.core.domain.usecase.GetBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetEmployeesBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetInvoicesUseCase
import com.casecode.pos.core.domain.usecase.GetItemsUseCase
import com.casecode.pos.core.domain.usecase.GetSubscriptionsUseCase
import com.casecode.pos.core.domain.usecase.GetTodayInvoicesUseCase
import com.casecode.pos.core.domain.usecase.ItemImageUseCase
import com.casecode.pos.core.domain.usecase.SetBusinessUseCase
import com.casecode.pos.core.domain.usecase.SetSubscriptionBusinessUseCase
import com.casecode.pos.core.domain.usecase.UpdateEmployeesUseCase
import com.casecode.pos.core.domain.usecase.UpdateItemUseCase
import com.casecode.pos.core.domain.usecase.UpdateStockInItemsUseCase
import com.casecode.pos.core.testing.repository.TestAccountRepository
import com.casecode.pos.core.testing.repository.TestAuthRepository
import com.casecode.pos.core.testing.repository.TestBusinessRepository
import com.casecode.pos.core.testing.repository.TestEmployeesBusinessRepository
import com.casecode.pos.core.testing.repository.TestInvoiceRepository
import com.casecode.pos.core.testing.repository.TestItemImageRepository
import com.casecode.pos.core.testing.repository.TestItemRepository
import com.casecode.pos.core.testing.repository.TestSubscriptionsBusinessRepository
import com.casecode.pos.core.testing.repository.TestSubscriptionsRepository
import com.casecode.pos.core.testing.util.CoroutinesTestRule
import com.casecode.pos.core.testing.util.TestNetworkMonitor
import org.junit.Before
import org.junit.Rule

abstract class BaseTest {
    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    // Repo
    lateinit var accountService: TestAccountRepository
    lateinit var authService: TestAuthRepository

    lateinit var networkMonitor: TestNetworkMonitor
    lateinit var businessRepository: TestBusinessRepository
    lateinit var subscriptionsRepository: TestSubscriptionsRepository
    lateinit var subscriptionsBusinessRepository: TestSubscriptionsBusinessRepository
    lateinit var employeesBusinessRepository: TestEmployeesBusinessRepository
    lateinit var itemRepository: TestItemRepository
    lateinit var imageRepository: TestItemImageRepository
    lateinit var invoiceRepository: TestInvoiceRepository

    // Use cases
    lateinit var getBusiness: GetBusinessUseCase
    lateinit var setBusiness: SetBusinessUseCase
    lateinit var getEmployees: GetEmployeesBusinessUseCase
    lateinit var addEmployee: AddEmployeeUseCase
    lateinit var updateEmployee: UpdateEmployeesUseCase
    lateinit var completeBusiness: CompleteBusinessUseCase
    lateinit var getSubscriptions: GetSubscriptionsUseCase
    lateinit var setSubscription: SetSubscriptionBusinessUseCase
    lateinit var setEmployees: AddEmployeesBusinessUseCase
    lateinit var getImage: ItemImageUseCase
    lateinit var getItems: GetItemsUseCase
    lateinit var addItem: AddItemUseCase
    lateinit var updateItem: UpdateItemUseCase
    lateinit var deleteItem: DeleteItemUseCase
    lateinit var addInvoice: AddInvoiceUseCase
    lateinit var updateStockInItem: UpdateStockInItemsUseCase
    lateinit var getInvoices: GetInvoicesUseCase
    lateinit var getTodayInvoices: GetTodayInvoicesUseCase

    @Before
    fun setup() {
        networkMonitor = TestNetworkMonitor()

        accountService = TestAccountRepository()
        authService = TestAuthRepository()
        businessRepository = TestBusinessRepository()
        subscriptionsRepository = TestSubscriptionsRepository()
        subscriptionsBusinessRepository = TestSubscriptionsBusinessRepository()
        employeesBusinessRepository = TestEmployeesBusinessRepository()

        // Items repo
        itemRepository = TestItemRepository()
        imageRepository = TestItemImageRepository()

        // Invoice repo
        invoiceRepository = TestInvoiceRepository()

        // use cases
        getBusiness = GetBusinessUseCase(businessRepository)
        setBusiness = SetBusinessUseCase(businessRepository)
        getEmployees = GetEmployeesBusinessUseCase(employeesBusinessRepository)
        addEmployee = AddEmployeeUseCase(employeesBusinessRepository)
        updateEmployee = UpdateEmployeesUseCase(employeesBusinessRepository)
        getSubscriptions = GetSubscriptionsUseCase(subscriptionsRepository)
        completeBusiness = CompleteBusinessUseCase(businessRepository)
        setSubscription =
            SetSubscriptionBusinessUseCase(subscriptionsBusinessRepository)
        setEmployees = AddEmployeesBusinessUseCase(employeesBusinessRepository)

        // Items use cases
        getImage = ItemImageUseCase(imageRepository)
        getItems = GetItemsUseCase(itemRepository)
        addItem = AddItemUseCase(itemRepository)
        updateItem = UpdateItemUseCase(itemRepository)
        deleteItem = DeleteItemUseCase(itemRepository)
        updateStockInItem = UpdateStockInItemsUseCase(itemRepository)

        // Invoice use cases
        addInvoice = AddInvoiceUseCase(invoiceRepository)
        getInvoices = GetInvoicesUseCase(invoiceRepository)
        getTodayInvoices = GetTodayInvoicesUseCase(invoiceRepository)
        init()
    }

    abstract fun init()
}