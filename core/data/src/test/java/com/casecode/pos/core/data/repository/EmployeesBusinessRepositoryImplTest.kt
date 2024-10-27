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
package com.casecode.pos.core.data.repository

import com.casecode.pos.core.model.data.users.Employee

class EmployeesBusinessRepositoryImplTest {
    /*   private var firestore: FirebaseFirestore = mockk<FirebaseFirestore>()

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
           }*/

    private fun createValidEmployees(): MutableList<Employee> {
        // Create a valid Business object for testing
        // Replace with your actual business data
        return mutableListOf(
            Employee("name", "123434", "123243242", "branch1", "admin"),
            Employee("name", "123434", "123243242", "branch1", "admin"),
        )
    }
}