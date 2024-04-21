package com.casecode.pos.viewmodel

import com.casecode.domain.model.subscriptions.Subscription
import com.casecode.domain.model.users.Branch
import com.casecode.domain.model.users.Employee
import com.casecode.domain.model.users.StoreType
import com.casecode.domain.repository.AddBusiness
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.testing.base.BaseTest
import com.casecode.testing.util.MainDispatcherRule
import com.casecode.testing.util.getOrAwaitValue
import com.google.firebase.auth.FirebaseAuth
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

/**
 * A JUnit test class for the [StepperBusinessViewModel] class.
 *
 */
class StepperBusinessViewModelTest : BaseTest() {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // subject under test
    private lateinit var businessViewModel: StepperBusinessViewModel
    private val firebaseAuth: FirebaseAuth = mockk()

    override fun init() {
        every { firebaseAuth.currentUser?.uid } returns "test"
        businessViewModel =
            StepperBusinessViewModel(
                testNetworkMonitor,
                firebaseAuth,
                signOutUseCase,
                setBusinessUseCase,
                completeBusinessUseCase,
                getSubscriptionsUseCase,
                setSubscriptionBusinessUseCase,
                setEmployeesBusinessUseCase,
            )
    }

    @Test
    fun setStoreType_whenStoreTypeArabic_returnStoreTypeEnglish() {
        // Given store type arabic
        val storeTypeAr = "قهوة"

        // When sent store type arabic
        businessViewModel.setStoreType(storeTypeAr)
        val storeTypeEn = businessViewModel.addBusiness().storeType

        // Then result is name of store english only
        assertThat(storeTypeEn, `is`(StoreType.Coffee))
    }

    @Test
    fun addBranch_WhenHasBranch_returnsTrueAndMessageSuccess() {
        // Given - add Branch
        businessViewModel.setBranchName("branch1")
        businessViewModel.setBranchPhone("123456")

        // When - add branch
        businessViewModel.addBranch()
        // Then - returns string branch success and true for is add branch.
        assertThat(
            R.string.add_branch_success,
            `is`(businessViewModel.userMessage.value?.peekContent()),
        )
        assertThat(businessViewModel.isAddBranch.value?.peekContent(), `is`(true))
    }

    @Test
    fun updateBranch_whenChangeName_returnUpdateBranchIsTrueAndSuccess() {
        // Given
        val branch = Branch(1, "branch1", "1234")
        businessViewModel.branches.value = arrayListOf(branch)
        businessViewModel.setBranchSelected(branch)
        businessViewModel.setBranchName("branch2")

        // When
        businessViewModel.updateBranch()

        // Then
        assertThat(
            R.string.update_branch_success,
            `is`(businessViewModel.userMessage.value?.peekContent()),
        )
        assertThat(businessViewModel.isUpdateBranch.value?.peekContent(), `is`(true))
    }

    @Test
    fun updateBranch_whenNotChange_returnUpdateBranchIsFalseAndFailed() {
        // Given
        val branch = Branch(1, "branch1", "1234")
        businessViewModel.branches.value = arrayListOf(branch)
        businessViewModel.setBranchSelected(branch)
        businessViewModel.setBranchName(branch.branchName!!)
        businessViewModel.setBranchPhone(branch.phoneNumber!!)

        // When
        businessViewModel.updateBranch()

        // Then
        assertThat(
            R.string.update_branch_fail,
            `is`(businessViewModel.userMessage.value?.peekContent()),
        )
        assertThat(businessViewModel.isUpdateBranch.value?.peekContent(), `is`(false))
    }

    @Test
    fun `updateBranch when branches is out of index return failed and false`() {
        // Given
        val branch = Branch(1, "branch1", "1234")
        businessViewModel.setBranchSelected(branch)

        // When
        businessViewModel.updateBranch()

        // Then
        assertThat(
            R.string.update_branch_fail,
            `is`(businessViewModel.userMessage.value?.peekContent()),
        )
        assertThat(businessViewModel.isUpdateBranch.value?.peekContent(), `is`(false))
    }

    @Test
    fun addBusiness_shouldReturnSuccessBusiness() =
        runTest {
            // Given
            val storeType = StoreType.Clothes.toString()
            val email = "test@email.com"
            val phone = "1234567890"
            val branches =
                arrayListOf(Branch(1, "Branch 1", "1234567890"), Branch(2, "Branch 2", "9876543210"))
            // handle network monitor available to true
            testNetworkMonitor.setConnected(true)
            businessViewModel.setNetworkMonitor()
            businessViewModel.setStoreType(storeType)
            businessViewModel.setEmail(email)
            businessViewModel.setPhoneBusiness(phone)
            businessViewModel.branches.value = branches
            businessViewModel.setCurrentUid("test")

            // When - add new business
            businessViewModel.setBusiness()

            var isSuccess = AddBusiness.success(false)
            val job =
                launch {
                    isSuccess = businessViewModel.isAddBusiness.value as AddBusiness
                }

            // Execute pending coroutines actions
            advanceUntilIdle()

            // Then -
            assertThat(isSuccess, equalTo(AddBusiness.success(true)))
            job.cancel()
        }

    @Test
    fun setBusinessUseCase_whenNetworkIsUnavailable_thenReturnMessageNetworkError() {
        // Given - network unAvailable
        testNetworkMonitor.setConnected(false)
        businessViewModel.setNetworkMonitor()

        // When - add new business
        businessViewModel.setBusiness()

        // Then
        assertThat(businessViewModel.userMessage.value?.peekContent(), `is`(R.string.network_error))
    }

    @Test
    fun getSubscriptionBusiness_WhenHasListOfSubscription_thenReturnSuccess() =
        runTest {
            // Given
            testNetworkMonitor.setConnected(true)
            businessViewModel.setNetworkMonitor()

            val actualSubscriptions = subscriptionsFake()
            testSubscriptionsRepository.sendSubscriptions(actualSubscriptions)

            // When
            businessViewModel.getSubscriptionsBusiness()
            // Execute pending coroutines actions
            advanceUntilIdle()

            val result = businessViewModel.subscriptions.getOrAwaitValue()

            // Then
            assertThat(result, `is`(actualSubscriptions))
        }

    @Test
    fun getSubscriptionsBusiness_whenHasError_thenReturnEmptyList() =
        runTest {
            // Given
            testSubscriptionsRepository.setReturnError(true)

            // When
            businessViewModel.getSubscriptionsBusiness()
            val result = businessViewModel.subscriptions.getOrAwaitValue()
            // Then
            assertThat(result, `is`(emptyList()))
        }

    @Test
    fun getSubscriptionBusiness_whenEmptyList_thenReturnEmptyList() =
        runTest {
            // Given
            testSubscriptionsRepository.setReturnEmpty(true)
            // When
            businessViewModel.getSubscriptionsBusiness()

            val result = businessViewModel.subscriptions.getOrAwaitValue()
            // Then
            assertThat(result, `is`(emptyList()))
        }

    @Test
    fun addSubscriptionBusiness_whenNetworkIsUnavailable_thenMessageNetworkError() =
        runTest {
            businessViewModel.setConnected(false)

            businessViewModel.checkNetworkThenSetSubscriptionBusinessSelected()

            assertThat(businessViewModel.userMessage.value?.peekContent(), `is`(R.string.network_error))
        }

    @Test
    fun addSubscriptionBusiness_whenNetworkIsAvailable_thenReturnsSuccess() =
        runTest {
            // Given
            businessViewModel.addSubscriptionBusinessSelected(
                Subscription(
                    30,
                    30,
                    listOf("admin", "sale"),
                    "basic",
                ),
            )
            businessViewModel.setConnected(true)

            businessViewModel.checkNetworkThenSetSubscriptionBusinessSelected()

            assertThat(
                businessViewModel.isAddSubscriptionBusiness.getOrAwaitValue(),
                `is`(Resource.success(true)),
            )
        }

    @Test
    fun `addEmployee() with new employee should update employees list and set isAddEmployee to true`() {
        // Arrange
        val name = "mahmoud"
        val phone = "12345"
        val password = "12345m"
        val branchName = "Branch 1"
        val permission = "Admin"
        val employee = Employee(name, phone, password, branchName, permission)
        val employeesValue = ArrayList<Employee>()

        // Act
        businessViewModel.addEmployee(name, phone, password, branchName, permission)

        // Assert
        assertThat(
            businessViewModel.employees.getOrAwaitValue(),
            `is`(employeesValue.plus(employee)),
        )
        assertThat(businessViewModel.isAddEmployee.value?.peekContent(), `is`(true))
    }

    @Test
    fun `addEmployee() with existing employee name should not update employees list and set isAddEmployee to false`() {
        // Arrange
        val name = "mahmoud"
        val phone = "12345"
        val password = "12345m"
        val branchName = "Branch 1"
        val permission = "Admin"
        val employee = Employee(name, phone, password, branchName, permission)
        val employeesValue = ArrayList<Employee>()
        employeesValue.add(employee)

        // Act
        businessViewModel.addEmployee(name, phone, password, branchName, permission)
        businessViewModel.addEmployee(name, phone, password, branchName, permission)

        // Assert
        assertThat(
            businessViewModel.employees.getOrAwaitValue(),
            `is`(employeesValue),
        )
        assertThat(businessViewModel.isAddEmployee.value?.peekContent(), `is`(false))
    }

    @Test
    fun `updateEmployee() with different employee should update employees list and set isUpdateEmployee to true`()  {
        // Arrange
        val name = "mahmoud"
        val phone = "12345"
        val password = "12345m"
        val branchName = "Branch 1"
        val permission = "Admin"
        businessViewModel.addEmployee(name, phone, password, branchName, permission)
        businessViewModel.setEmployeeSelected(businessViewModel.employees.value?.first() ?: return)
        // Act - when update employee
        businessViewModel.updateEmployee("nameUpdated", phone, password, branchName, permission)

        // Assert
        assertThat(businessViewModel.isUpdateEmployee.value?.peekContent(), `is`(true))
    }

    @Test
    fun `updateEmployee() with same name employee shouldn't update employees list and set isUpdateEmployee to false`()  {
        // Arrange
        val name = "mahmoud"
        val phone = "12345"
        val password = "12345m"
        val branchName = "Branch 1"
        val permission = "Admin"
        businessViewModel.addEmployee(name, phone, password, branchName, permission)
        businessViewModel.addEmployee("name2", phone, password, branchName, permission)
        businessViewModel.addEmployee("name3", phone, password, branchName, permission)
        businessViewModel.setEmployeeSelected(businessViewModel.employees.value?.first() ?: return)

        // Act - when update employee
        businessViewModel.updateEmployee("name2", "121231423", password, branchName, permission)

        // Assert
        assertThat(businessViewModel.isUpdateEmployee.value?.peekContent(), `is`(false))
    }

    @Test
    fun `updateEmployee() with same employee shouldn't update employees list and set isUpdateEmployee to false`()  {
        // Arrange
        val name = "mahmoud"
        val phone = "12345"
        val password = "12345m"
        val branchName = "Branch 1"
        val permission = "Admin"
        businessViewModel.addEmployee(name, phone, password, branchName, permission)
        businessViewModel.setEmployeeSelected(businessViewModel.employees.value?.first() ?: return)
        // Act - when update employee
        businessViewModel.updateEmployee(name, phone, password, branchName, permission)

        // Assert
        assertThat(businessViewModel.isUpdateEmployee.value?.peekContent(), `is`(false))
    }

    @Test
    @Ignore("issue test: when out of index can't work, can't find why issue happened")
    fun `updateEmployee()  shouldn't update employees list and set isUpdateEmployee to false`()  {
        // Arrange
        val name = "mahmoud"
        val phone = "12345"
        val password = "12345m"
        val branchName = "Branch 1"
        val permission = "Admin"
        businessViewModel.addEmployee(name, phone, password, branchName, permission)

        businessViewModel.setEmployeeSelected(Employee())

        // Act - when update employee
        businessViewModel.updateEmployee("name2", "121231423", password, branchName, permission)

        // Assert
        assertThat(businessViewModel.isUpdateEmployee.getOrAwaitValue().peekContent(), `is`(false))
    }

    @Test
    fun setEmployeesBusiness_whenNetworkIsAvailable_thenReturnsSuccess() =
        runTest {
            // Given
            val name = "mahmoud"
            val phone = "12345"
            val password = "12345m"
            val branchName = "Branch 1"
            val permission = "Admin"
            businessViewModel.addEmployee(name, phone, password, branchName, permission)
            businessViewModel.setConnected(true)

            // When
            businessViewModel.checkNetworkThenSetEmployees()

            // Then
            assertThat(
                businessViewModel.isAddEmployees.getOrAwaitValue(),
                `is`(Resource.success(true)),
            )
        }

    @Test
    fun setEmployeesBusiness_whenNetworkIsUnavailable_thenReturnMessageNetworkError() =
        runTest {
            // Given
            val name = "mahmoud"
            val phone = "12345"
            val password = "12345m"
            val branchName = "Branch 1"
            val permission = "Admin"
            businessViewModel.addEmployee(name, phone, password, branchName, permission)
            businessViewModel.setConnected(false)

            // When
            businessViewModel.checkNetworkThenSetEmployees()

            // Then
            assertThat(
                businessViewModel.userMessage.value?.peekContent(),
                `is`(R.string.network_error),
            )
        }

    private fun subscriptionsFake(): List<Subscription> {
        return listOf(
            Subscription(
                duration = 30,
                cost = 0,
                type = "basic",
                permissions = listOf("write", "read", "admin"),
            ),
            Subscription(
                duration = 30,
                cost = 20,
                type = "pro",
                permissions = listOf("write", "read", "admin"),
            ),
            Subscription(
                duration = 90,
                cost = 60,
                type = "premium",
                permissions = listOf("write", "read", "admin"),
            ),
        )
    }
}