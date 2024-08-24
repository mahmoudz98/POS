package com.casecode.pos.core.data.repository

import com.casecode.pos.core.data.R
import com.casecode.pos.core.data.model.asExternalEmployees
import com.casecode.pos.core.data.utils.USERS_COLLECTION_PATH
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Employee
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

class EmployeesBusinessRepositoryImplTest {
    private var firestore: FirebaseFirestore = mockk<FirebaseFirestore>()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope: TestScope = TestScope(testDispatcher)

    // subject under test
    private lateinit var employeesBusinessRepositoryImpl: EmployeesBusinessRepositoryImpl

    private val uid = "test"

    // Capture the success and failure listeners
    private val successListenerSlot = slot<OnSuccessListener<Void>>()
    private val failureListenerSlot = slot<OnFailureListener>()

    @Before
    fun setup() {
        // employeesBusinessRepositoryImpl = EmployeesBusinessRepositoryImpl(firestore, testDispatcher)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun setEmployeesBusinessData_success() =
        testScope.runTest {
            // Arrange
            val employees = createValidEmployees()

            // mock firestore behavior
            every {
                firestore
                    .collection(USERS_COLLECTION_PATH)
                    .document(uid)
                    .update(employees.asExternalEmployees() as Map<String, Any>)
                    .addOnSuccessListener(capture(successListenerSlot))
                    .addOnFailureListener(capture(failureListenerSlot))
            } answers {
                // Simulate a successful operation by invoking the success listener with null
                successListenerSlot.captured.onSuccess(null)
                // Return a mock Task
                mockk<Task<Void>>()
            }
            // Act
            val resultEmployees = employeesBusinessRepositoryImpl.setEmployees(employees)

            // Assert
            val expectedResult = Resource.success(true)
            assertThat(resultEmployees, `is`(expectedResult))
        }

    @Test
    fun setEmployeesBusinessData_failure() =
        testScope.runTest {
            // Arrange
            val employees = createValidEmployees()
            // Mock firestore behavior
            every {
                firestore
                    .collection(USERS_COLLECTION_PATH)
                    .document(uid)
                    .update(employees.asExternalEmployees() as Map<String, Any>)
                    .addOnSuccessListener(capture(successListenerSlot))
                    .addOnFailureListener(capture(failureListenerSlot))
            } answers {
                // Simulate a failed operation by invoking the failure listener with an exception
                failureListenerSlot.captured.onFailure(Exception())
                // Return a mock Task
                mockk<Task<Void>>()
            }
            // Act
            val resultEmployees = employeesBusinessRepositoryImpl.setEmployees(employees)
            // Assert
            val expectedResult = Resource.error<Boolean>(R.string.core_data_add_employees_business_failure)
            assertThat(resultEmployees, `is`(expectedResult))
        }

    @Test
    fun setEmployeesBusinessData_shouldReturnErrorUnKnowHostException() =
        testScope.runTest {
            // Arrange
            val employees = createValidEmployees()
            // Mock firestore behavior
            every {
                firestore
                    .collection(USERS_COLLECTION_PATH)
                    .document(uid)
                    .update(employees.asExternalEmployees() as Map<String, Any>)
                    .addOnSuccessListener(capture(successListenerSlot))
                    .addOnFailureListener(capture(failureListenerSlot))
            } answers {
                throw UnknownHostException("error network")
            }
            // Act
            val resultEmployees = employeesBusinessRepositoryImpl.setEmployees(employees)
            // Assert
            val expectedResult = Resource.error<Boolean>(R.string.core_data_add_employees_business_network)
            assertThat(resultEmployees, `is`(expectedResult))
        }

    private fun createValidEmployees(): MutableList<Employee> {
        // Create a valid Business object for testing
        // Replace with your actual business data
        return mutableListOf(
            Employee("name", "123434", "123243242", "branch1", "admin"),
            Employee("name", "123434", "123243242", "branch1", "admin"),
        )
    }
}