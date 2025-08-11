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

import com.casecode.pos.core.domain.usecase.SignInEmployeeUseCase
import com.casecode.pos.core.domain.usecase.SignInOwnerUseCase
import com.casecode.pos.core.domain.usecase.SignOutUseCase
import com.casecode.pos.core.domain.usecase.old.AddInvoiceUseCase
import com.casecode.pos.core.domain.usecase.old.AddItemUseCase
import com.casecode.pos.core.domain.usecase.old.DeleteItemUseCase
import com.casecode.pos.core.domain.usecase.old.GetInvoicesUseCase
import com.casecode.pos.core.domain.usecase.old.GetItemsUseCase
import com.casecode.pos.core.domain.usecase.old.GetTodayInvoicesUseCase
import com.casecode.pos.core.domain.usecase.old.ItemImageUseCase
import com.casecode.pos.core.domain.usecase.old.UpdateItemUseCase
import com.casecode.pos.core.domain.usecase.old.UpdateStockInItemsUseCase
import com.casecode.pos.core.testing.repository.TestInvoiceRepository
import com.casecode.pos.core.testing.repository.TestItemImageRepository
import com.casecode.pos.core.testing.repository.TestItemRepository
import com.casecode.pos.core.testing.repository.business.TestAuthRepository
import com.casecode.pos.core.testing.repository.business.TestBranchRepository
import com.casecode.pos.core.testing.repository.business.TestBusinessRepository
import com.casecode.pos.core.testing.repository.business.TestEmployeeRepository
import com.casecode.pos.core.testing.repository.business.TestSessionRepository
import com.casecode.pos.core.testing.services.TestLogService
import com.casecode.pos.core.testing.util.CoroutinesTestRule
import com.casecode.pos.core.testing.util.TestNetworkMonitor
import org.junit.Before
import org.junit.Rule

abstract class BaseTest {
    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    lateinit var networkMonitor: TestNetworkMonitor
    lateinit var fakeLogService: TestLogService

    // Repo
    lateinit var testAuthRepository: TestAuthRepository
    lateinit var testBusinessRepository: TestBusinessRepository
    lateinit var testBranchRepository: TestBranchRepository
    lateinit var testEmployeeRepository: TestEmployeeRepository
    lateinit var testSessionRepository: TestSessionRepository

    lateinit var itemRepository: TestItemRepository
    lateinit var imageRepository: TestItemImageRepository
    lateinit var invoiceRepository: TestInvoiceRepository

    // Use cases

    lateinit var signInOwnerUseCase: SignInOwnerUseCase
    lateinit var signInEmployeeUseCase: SignInEmployeeUseCase
    lateinit var signOutUseCase: SignOutUseCase
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

        testAuthRepository = TestAuthRepository()
        testBusinessRepository = TestBusinessRepository()
        testBranchRepository = TestBranchRepository()
        testEmployeeRepository = TestEmployeeRepository()
        testSessionRepository = TestSessionRepository()
        fakeLogService = TestLogService()

        // Items repo
        itemRepository = TestItemRepository()
        imageRepository = TestItemImageRepository()

        // Invoice repo
        invoiceRepository = TestInvoiceRepository()

        // use cases
        signInOwnerUseCase = SignInOwnerUseCase(
            authRepository = testAuthRepository,
            businessRepository = testBusinessRepository,
            branchRepository = testBranchRepository,
            sessionRepository = testSessionRepository,
            logService = fakeLogService,
        )

        signInEmployeeUseCase = SignInEmployeeUseCase(
            employeeRepository = testEmployeeRepository,
            sessionRepository = testSessionRepository,
            logService = fakeLogService,
        )

        signOutUseCase = SignOutUseCase(
            authRepository = testAuthRepository,
            sessionRepository = testSessionRepository,
            logService = fakeLogService,
        )

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