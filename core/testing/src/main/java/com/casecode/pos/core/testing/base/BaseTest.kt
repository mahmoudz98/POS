package com.casecode.pos.core.testing.base

import com.casecode.pos.core.domain.usecase.AddEmployeesUseCase
import com.casecode.pos.core.domain.usecase.AddInvoiceUseCase
import com.casecode.pos.core.domain.usecase.AddItemUseCase
import com.casecode.pos.core.domain.usecase.CompleteBusinessUseCase
import com.casecode.pos.core.domain.usecase.DeleteItemUseCase
import com.casecode.pos.core.domain.usecase.GetBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetEmployeesBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetInvoicesUseCase
import com.casecode.pos.core.domain.usecase.GetItemsUseCase
import com.casecode.pos.core.domain.usecase.GetSubscriptionsUseCase
import com.casecode.pos.core.domain.usecase.ItemImageUseCase
import com.casecode.pos.core.domain.usecase.SetBusinessUseCase
import com.casecode.pos.core.domain.usecase.SetEmployeesBusinessUseCase
import com.casecode.pos.core.domain.usecase.SetSubscriptionBusinessUseCase
import com.casecode.pos.core.domain.usecase.UpdateEmployeesUseCase
import com.casecode.pos.core.domain.usecase.UpdateItemUseCase
import com.casecode.pos.core.domain.usecase.UpdateStockInItemsUseCase
import com.casecode.pos.core.testing.repository.TestBusinessRepository
import com.casecode.pos.core.testing.repository.TestEmployeesBusinessRepository
import com.casecode.pos.core.testing.repository.TestInvoiceRepository
import com.casecode.pos.core.testing.repository.TestItemImageRepository
import com.casecode.pos.core.testing.repository.TestItemRepository
import com.casecode.pos.core.testing.repository.TestSubscriptionsBusinessRepository
import com.casecode.pos.core.testing.repository.TestSubscriptionsRepository
import com.casecode.pos.core.testing.service.TestAccountService
import com.casecode.pos.core.testing.service.TestAuthService
import com.casecode.pos.core.testing.util.CoroutinesTestRule
import com.casecode.pos.core.testing.util.TestNetworkMonitor

import org.junit.Before
import org.junit.Rule

abstract class BaseTest {
    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = CoroutinesTestRule()


    // Repo
    lateinit var testAccountService: TestAccountService
    lateinit var testAuthService: TestAuthService

    lateinit var testNetworkMonitor: TestNetworkMonitor
    lateinit var testBusinessRepository: TestBusinessRepository
    lateinit var testSubscriptionsRepository: TestSubscriptionsRepository
    lateinit var testSubscriptionsBusinessRepository: TestSubscriptionsBusinessRepository
    lateinit var testEmployeesBusinessRepository: TestEmployeesBusinessRepository
    lateinit var testItemRepository: TestItemRepository
    lateinit var testImageRepository: TestItemImageRepository
    lateinit var testInvoiceRepository: TestInvoiceRepository
    // Use cases
    lateinit var getBusinessUseCase: GetBusinessUseCase
    lateinit var setBusinessUseCase: SetBusinessUseCase
    lateinit var getEmployeesBusinessUseCase: GetEmployeesBusinessUseCase
    lateinit var addEmployeesUseCase: AddEmployeesUseCase
    lateinit var updateEmployeesUseCase: UpdateEmployeesUseCase
    lateinit var completeBusinessUseCase: CompleteBusinessUseCase
    lateinit var getSubscriptionsUseCase: GetSubscriptionsUseCase
    lateinit var setSubscriptionBusinessUseCase: SetSubscriptionBusinessUseCase
    lateinit var setEmployeesBusinessUseCase: SetEmployeesBusinessUseCase
    lateinit var imageUseCase: ItemImageUseCase
    lateinit var getItemsUseCase: GetItemsUseCase
    lateinit var addItemsUseCase: AddItemUseCase
    lateinit var updateItemsUseCase: UpdateItemUseCase
    lateinit var deleteItemUseCase: DeleteItemUseCase
    lateinit var addInvoiceUseCase: AddInvoiceUseCase
    lateinit var updateStockInItemsUseCase: UpdateStockInItemsUseCase
    lateinit var getInvoicesUseCase: GetInvoicesUseCase

    @Before
    fun setup() {
        testNetworkMonitor = TestNetworkMonitor()
        // Repo
        // Auth
        testAccountService = TestAccountService()
        testAuthService = TestAuthService()
        testBusinessRepository = TestBusinessRepository()
        testSubscriptionsRepository = TestSubscriptionsRepository()
        testSubscriptionsBusinessRepository = TestSubscriptionsBusinessRepository()
        testEmployeesBusinessRepository = TestEmployeesBusinessRepository()

        // Items repo
        testItemRepository = TestItemRepository()
        testImageRepository = TestItemImageRepository()

        // Invoice repo
        testInvoiceRepository = TestInvoiceRepository()

        // use cases
        getBusinessUseCase = GetBusinessUseCase(testBusinessRepository)
        setBusinessUseCase = SetBusinessUseCase(testBusinessRepository)
        getEmployeesBusinessUseCase = GetEmployeesBusinessUseCase(testEmployeesBusinessRepository)
        addEmployeesUseCase = AddEmployeesUseCase(testEmployeesBusinessRepository)
        updateEmployeesUseCase = UpdateEmployeesUseCase(testEmployeesBusinessRepository)
        getSubscriptionsUseCase = GetSubscriptionsUseCase(testSubscriptionsRepository)
        completeBusinessUseCase = CompleteBusinessUseCase(testBusinessRepository)
        setSubscriptionBusinessUseCase =
            SetSubscriptionBusinessUseCase(testSubscriptionsBusinessRepository)
        setEmployeesBusinessUseCase = SetEmployeesBusinessUseCase(testEmployeesBusinessRepository)

        // Items use cases
        imageUseCase = ItemImageUseCase(testImageRepository)
        getItemsUseCase = GetItemsUseCase(testItemRepository)
        addItemsUseCase = AddItemUseCase(testItemRepository)
        updateItemsUseCase = UpdateItemUseCase(testItemRepository)
        deleteItemUseCase = DeleteItemUseCase(testItemRepository)
        updateStockInItemsUseCase = UpdateStockInItemsUseCase(testItemRepository)

        // Invoice use cases
        addInvoiceUseCase = AddInvoiceUseCase(testInvoiceRepository)
        getInvoicesUseCase = GetInvoicesUseCase(testInvoiceRepository)
        init()
    }

    abstract fun init()
}