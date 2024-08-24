package com.casecode.pos.feature.stepper

import com.casecode.pos.core.model.data.subscriptions.Subscription
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.model.data.users.Employee
import com.casecode.pos.core.model.data.users.StoreType
import com.casecode.pos.core.testing.base.BaseTest
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.casecode.pos.core.ui.R.string as uiString

/**
 * A JUnit test class for the [StepperBusinessViewModel] class.
 *
 */
class StepperBusinessViewModelTest : BaseTest() {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // subject under test
    private lateinit var businessViewModel: StepperBusinessViewModel

    override fun init() {
        businessViewModel =
            StepperBusinessViewModel(
                networkMonitor,
                accountService,
                setBusiness,
                completeBusiness,
                getSubscriptions,
                setSubscription,
                setEmployees,
            )
    }

    @Test
    fun setStoreType_whenStoreTypeArabic_returnStoreTypeEnglish() {
        // Given store type arabic
        val storeTypeAr = "قهوة"

        // When sent store type arabic
        businessViewModel.setBusinessInfo(storeTypeAr, "", "")
        val storeTypeEn = businessViewModel.addBusiness().storeType

        // Then result is name of store english only
        assertThat(storeTypeEn, `is`(StoreType.Coffee))
    }

    @Test
    fun addBranch_WhenHasBranch_returnAddSuccess() {
        // When - add branch
        businessViewModel.addBranch("branch1", "123456")
        // Then - returns string branch success .
        assertThat(
            uiString.core_ui_success_add_branch_message,
            `is`(businessViewModel.userMessage.value),
        )
    }

    @Test
    fun updateBranch_whenChangeName_returnUpdateBranchMessageSuccess() {
        // Given - a new branch and selected branch
        businessViewModel.addBranch("branch1", "123456")
        businessViewModel.setBranchSelected(businessViewModel.uiState.value.branches[0])

        // When
        businessViewModel.updateBranch("branch2", "123456")

        // Then
        assertThat(
            uiString.core_ui_success_update_branch_message,
            `is`(businessViewModel.userMessage.value),
        )
    }

    @Test
    fun updateBranch_whenNotChange_returnUpdateBranchFailed() {
        // Given a new Branch
        businessViewModel.addBranch("branch1", "123456")
        businessViewModel.setBranchSelected(businessViewModel.uiState.value.branches[0])

        // When
        businessViewModel.updateBranch("branch1", "123456")

        // Then
        assertThat(
            uiString.core_ui_error_update_branch_message,
            `is`(businessViewModel.userMessage.value),
        )
    }

    @Test
    fun `updateBranch when branches is out of index return failed and false`() {
        // Given
        val branch = Branch(1, "branch1", "1234")
        businessViewModel.setBranchSelected(branch)

        // When
        businessViewModel.updateBranch("branch2", "123123")

        // Then
        assertThat(
            uiString.core_ui_error_update_branch_message,
            `is`(businessViewModel.userMessage.value),
        )
    }

    @Test
    fun addBusiness_returnMessageAddSuccessBusiness() =
        runTest {
            // Given
            val storeType = StoreType.Clothes.toString()
            val email = "test@email.com"
            val phone = "1234567890"
            businessViewModel.setBusinessInfo(storeType, email, phone)

            businessViewModel.addBranch("Branch 1", "1234567890")
            businessViewModel.addBranch("Branch 2", "1234567890")
            networkMonitor.setConnected(true)
            businessViewModel.networkMonitor()

            // When - add new business
            businessViewModel.setBusiness()

            var message: Int? = null
            val job =
                launch {
                    message = businessViewModel.userMessage.value
                }

            // Execute pending coroutines actions
            advanceUntilIdle()

            // Then -
            assertThat(message, equalTo(R.string.feature_stepper_success_add_business_message))
            job.cancel()
        }

    @Test
    fun setBusiness_whenNetworkIsUnavailable_thenReturnMessageNetworkError() {
        // Given - network unAvailable
        networkMonitor.setConnected(false)
        businessViewModel.networkMonitor()

        // When - add new business
        businessViewModel.setBusiness()

        // Then
        assertThat(businessViewModel.userMessage.value, `is`(uiString.core_ui_error_network))
    }

    @Test
    fun getSubscriptionBusiness_WhenHasListOfSubscription_thenReturnSuccess() =
        runTest {
            val collectJob =
                launch(UnconfinedTestDispatcher()) { businessViewModel.uiState.collect() }

            networkMonitor.setConnected(true)
            businessViewModel.networkMonitor()

            // When
            val actualSubscriptions = subscriptionsFake()
            subscriptionsRepository.sendSubscriptions(actualSubscriptions)
            businessViewModel.getSubscriptionsBusiness()
            advanceUntilIdle()
            assertEquals(actualSubscriptions, businessViewModel.uiState.value.subscriptions)
            collectJob.cancel()
        }

    @Test
    fun getSubscriptionsBusiness_whenHasError_thenReturnEmptyList() =
        runTest {
            val collectJob =
                launch(UnconfinedTestDispatcher()) { businessViewModel.uiState.collect() }

            // Given
            subscriptionsRepository.setReturnError(true)

            // When
            businessViewModel.getSubscriptionsBusiness()
            val result = businessViewModel.uiState.value.subscriptions
            // Then
            assertEquals(emptyList(), result)
            collectJob.cancel()
        }

    @Test
    fun getSubscriptionBusiness_whenEmptyList_thenReturnEmptyList() =
        runTest {
            // Given
            val collectJob =
                launch(UnconfinedTestDispatcher()) { businessViewModel.uiState.collect() }
            subscriptionsRepository.setReturnEmpty(true)
            // When
            businessViewModel.getSubscriptionsBusiness()
            val result = businessViewModel.uiState.value.subscriptions
            // Then
            assertEquals(emptyList(), result)
            collectJob.cancel()
        }

    @Test
    fun addSubscriptionBusiness_whenNetworkIsUnavailable_thenMessageNetworkError() =
        runTest {
            val collectJob =
                launch(UnconfinedTestDispatcher()) { businessViewModel.userMessage.collect() }

            businessViewModel.setConnected(false)

            businessViewModel.checkNetworkThenSetSubscriptionBusinessSelected()

            assertThat(businessViewModel.userMessage.value, `is`(uiString.core_ui_error_network))
            collectJob.cancel()
        }

    @Test
    fun addSubscriptionBusiness_whenNetworkIsAvailable_thenNextStepIsTrue() =
        runTest {
            // Given
            val collectJob =
                launch(UnconfinedTestDispatcher()) { businessViewModel.buttonStepState.collect() }
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

            assertTrue(businessViewModel.buttonStepState.value.buttonNextStep)
            collectJob.cancel()
        }

    @Test
    fun `addEmployee with new employee should update employees list and return message`() =
        runTest {
            val collectJob =
                launch(UnconfinedTestDispatcher()) { businessViewModel.uiState.collect() }
            val collectJob2 =
                launch(UnconfinedTestDispatcher()) { businessViewModel.userMessage.collect() }

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
                businessViewModel.uiState.value.employees,
                `is`(employeesValue.plus(employee)),
            )
            assertThat(
                businessViewModel.userMessage.value,
                `is`(uiString.core_ui_success_add_employee_message),
            )
            collectJob.cancel()
            collectJob2.cancel()
        }

    @Test
    fun `addEmployee() with existing employee name should not update employees list and return message duplicate`() =
        runTest {
            // Arrange
            val collectJob =
                launch(UnconfinedTestDispatcher()) { businessViewModel.uiState.collect() }
            val collectJob2 =
                launch(UnconfinedTestDispatcher()) { businessViewModel.userMessage.collect() }

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
                businessViewModel.uiState.value.employees,
                `is`(employeesValue),
            )
            assertThat(
                businessViewModel.userMessage.value,
                `is`(uiString.core_ui_error_employee_name_duplicate),
            )
            collectJob.cancel()
            collectJob2.cancel()
        }

    @Test
    fun `updateEmployee() with different employee should update employees list and return message updated`() =
        runTest {
            // Arrange
            val collectJob1 =
                launch(UnconfinedTestDispatcher()) { businessViewModel.userMessage.collect() }
            val collectJob2 =
                launch(UnconfinedTestDispatcher()) { businessViewModel.uiState.collect() }
            val name = "mahmoud"
            val phone = "12345"
            val password = "12345m"
            val branchName = "Branch 1"
            val permission = "Admin"
            businessViewModel.addEmployee(name, phone, password, branchName, permission)
            businessViewModel.setEmployeeSelected(
                businessViewModel.uiState.value.employees
                    .first(),
            )
            // Act - when update employee
            businessViewModel.updateEmployee("nameUpdated", phone, password, branchName, permission)

            // Assert
            assertThat(
                businessViewModel.userMessage.value,
                `is`(uiString.core_ui_success_update_employee_message),
            )
            collectJob1.cancel()
            collectJob2.cancel()
        }

    @Test
    fun `updateEmployee() with same name employee shouldn't update employees list and return message duplicate`() =
        runTest {
            // Arrange
            val collectJob1 =
                launch(UnconfinedTestDispatcher()) { businessViewModel.userMessage.collect() }
            val collectJob2 =
                launch(UnconfinedTestDispatcher()) { businessViewModel.uiState.collect() }
            val name = "mahmoud"
            val phone = "12345"
            val password = "12345m"
            val branchName = "Branch 1"
            val permission = "Admin"
            businessViewModel.addEmployee(name, phone, password, branchName, permission)
            businessViewModel.addEmployee("name2", phone, password, branchName, permission)
            businessViewModel.addEmployee("name3", phone, password, branchName, permission)
            businessViewModel.setEmployeeSelected(
                businessViewModel.uiState.value.employees
                    .first(),
            )

            // Act - when update employee
            businessViewModel.updateEmployee("name2", "121231423", password, branchName, permission)

            // Assert
            assertThat(
                businessViewModel.userMessage.value,
                `is`(uiString.core_ui_error_employee_name_duplicate),
            )
            collectJob1.cancel()
            collectJob2.cancel()
        }

    @Test
    fun `updateEmployee() with same employee shouldn't update employees list and `() {
        // Arrange
        val name = "mahmoud"
        val phone = "12345"
        val password = "12345m"
        val branchName = "Branch 1"
        val permission = "Admin"
        businessViewModel.addEmployee(name, phone, password, branchName, permission)
        businessViewModel.setEmployeeSelected(
            businessViewModel.uiState.value.employees
                .first(),
        )
        // Act - when update employee
        businessViewModel.updateEmployee(name, phone, password, branchName, permission)

        // Assert
        assertThat(
            businessViewModel.userMessage.value,
            `is`(uiString.core_ui_error_update_employee_message),
        )
    }

    @Test
    fun `updateEmployee()  shouldn't update employees list and set isUpdateEmployee to false`() {
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
        assertThat(
            businessViewModel.userMessage.value,
            `is`(uiString.core_ui_error_update_employee_message),
        )
    }

    @Test
    fun setEmployeesBusiness_whenNetworkIsAvailable_thenReturnsMessageCompleteBusinessStepSuccess() =
        runTest {
            val collectJob1 =
                launch(UnconfinedTestDispatcher()) { businessViewModel.userMessage.collect() }
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

            assertThat(
                businessViewModel.userMessage.value,
                `is`(R.string.feature_stepper_success_complete_business_setup_message),
            )

            collectJob1.cancel()
        }

    @Test
    fun setEmployeesBusiness_whenNetworkUnavailable_thenReturnMessageNetworkError() =
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
            assertThat(businessViewModel.userMessage.value, `is`(uiString.core_ui_error_network))
        }

    private fun subscriptionsFake(): List<Subscription> =
        listOf(
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