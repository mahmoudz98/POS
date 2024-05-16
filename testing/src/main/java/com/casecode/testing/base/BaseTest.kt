package com.casecode.testing.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.casecode.domain.usecase.AddEmployeesUseCase
import com.casecode.domain.usecase.AddInvoiceUseCase
import com.casecode.domain.usecase.AddItemUseCase
import com.casecode.domain.usecase.CompleteBusinessUseCase
import com.casecode.domain.usecase.DeleteItemUseCase
import com.casecode.domain.usecase.GetBusinessUseCase
import com.casecode.domain.usecase.GetEmployeesBusinessUseCase
import com.casecode.domain.usecase.GetInvoicesUseCase
import com.casecode.domain.usecase.GetItemsUseCase
import com.casecode.domain.usecase.GetSubscriptionsUseCase
import com.casecode.domain.usecase.ItemImageUseCase
import com.casecode.domain.usecase.SetBusinessUseCase
import com.casecode.domain.usecase.SetEmployeesBusinessUseCase
import com.casecode.domain.usecase.SetSubscriptionBusinessUseCase
import com.casecode.domain.usecase.SignOutUseCase
import com.casecode.domain.usecase.UpdateEmployeesUseCase
import com.casecode.domain.usecase.UpdateItemUseCase
import com.casecode.domain.usecase.UpdateStockInItemsUseCase
import com.casecode.testing.repository.TestBusinessRepository
import com.casecode.testing.repository.TestEmployeesBusinessRepository
import com.casecode.testing.repository.TestInvoiceRepository
import com.casecode.testing.repository.TestItemImageRepository
import com.casecode.testing.repository.TestItemRepository
import com.casecode.testing.repository.TestSubscriptionsBusinessRepository
import com.casecode.testing.repository.TestSubscriptionsRepository
import com.casecode.testing.util.CoroutinesTestRule
import com.casecode.testing.util.TestNetworkMonitor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

@ExperimentalCoroutinesApi
abstract class BaseTest {
    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = CoroutinesTestRule()

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Repo
    lateinit var testNetworkMonitor: TestNetworkMonitor
    lateinit var testBusinessRepository: TestBusinessRepository
    lateinit var testSubscriptionsRepository: TestSubscriptionsRepository
    lateinit var testSubscriptionsBusinessRepository: TestSubscriptionsBusinessRepository
    lateinit var testEmployeesBusinessRepository: TestEmployeesBusinessRepository
    lateinit var testItemRepository: TestItemRepository
    lateinit var testImageRepository: TestItemImageRepository
    lateinit var testInvoiceRepository: TestInvoiceRepository

    // Use cases
    lateinit var signOutUseCase: SignOutUseCase
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