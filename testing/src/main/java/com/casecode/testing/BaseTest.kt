package com.casecode.testing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.casecode.domain.usecase.CompleteBusinessUseCase
import com.casecode.domain.usecase.GetBusinessUseCase
import com.casecode.domain.usecase.GetSubscriptionsUseCase
import com.casecode.domain.usecase.SetBusinessUseCase
import com.casecode.domain.usecase.SetEmployeesBusinessUseCase
import com.casecode.domain.usecase.SetSubscriptionBusinessUseCase
import com.casecode.domain.usecase.SignOutUseCase
import com.casecode.testing.repository.TestBusinessRepository
import com.casecode.testing.repository.TestEmployeesBusinessRepository
import com.casecode.testing.repository.TestSignRepository
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
    private lateinit var testSignRepository: TestSignRepository
    private lateinit var testBusinessRepository: TestBusinessRepository
    lateinit var testSubscriptionsRepository: TestSubscriptionsRepository
    private lateinit var testSubscriptionsBusinessRepository: TestSubscriptionsBusinessRepository
    private lateinit var testEmployeesBusinessRepository: TestEmployeesBusinessRepository


    // Use cases
    lateinit var signOutUseCase: SignOutUseCase
    lateinit var getBusinessUseCase: GetBusinessUseCase
    lateinit var setBusinessUseCase: SetBusinessUseCase
    lateinit var completeBusinessUseCase: CompleteBusinessUseCase
    lateinit var getSubscriptionsUseCase: GetSubscriptionsUseCase
    lateinit var setSubscriptionBusinessUseCase: SetSubscriptionBusinessUseCase
    lateinit var setEmployeesBusinessUseCase: SetEmployeesBusinessUseCase

    @Before
    fun setup() {
        testNetworkMonitor = TestNetworkMonitor()
        testSignRepository = TestSignRepository()
        testBusinessRepository = TestBusinessRepository()
        testSubscriptionsRepository = TestSubscriptionsRepository()
        testSubscriptionsBusinessRepository = TestSubscriptionsBusinessRepository()
        testEmployeesBusinessRepository = TestEmployeesBusinessRepository()


        // use cases
        signOutUseCase = SignOutUseCase(testSignRepository)
        getBusinessUseCase = GetBusinessUseCase(testBusinessRepository)
        setBusinessUseCase = SetBusinessUseCase(testBusinessRepository)
        getSubscriptionsUseCase = GetSubscriptionsUseCase(testSubscriptionsRepository)
        completeBusinessUseCase = CompleteBusinessUseCase(testBusinessRepository)
        setSubscriptionBusinessUseCase =
            SetSubscriptionBusinessUseCase(testSubscriptionsBusinessRepository)
        setEmployeesBusinessUseCase = SetEmployeesBusinessUseCase(testEmployeesBusinessRepository)

        init()
    }

    abstract fun init()


    companion object {
        const val ERROR_MESSAGE_LIVEDATA_NULL = "LiveData has null value"
    }
}