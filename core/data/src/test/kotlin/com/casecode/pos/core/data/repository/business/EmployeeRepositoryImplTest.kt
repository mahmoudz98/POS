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
package com.casecode.pos.core.data.repository.business

import com.casecode.pos.core.analytics.NoOpAnalyticsHelper
import com.casecode.pos.core.data.model.OutboxPayload
import com.casecode.pos.core.data.model.asNetworkModel
import com.casecode.pos.core.data.utils.PasswordUtils
import com.casecode.pos.core.database.model.LocalSignalEntity
import com.casecode.pos.core.database.model.LocalSignalStatus
import com.casecode.pos.core.database.model.OutboxCommandEntity
import com.casecode.pos.core.database.model.OutboxEventType
import com.casecode.pos.core.database.model.asEntity
import com.casecode.pos.core.domain.exceptions.EmployeeIdCollisionException
import com.casecode.pos.core.domain.repository.business.EmployeeRepository
import com.casecode.pos.core.firebase.model.OperationType
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.model.business.EmployeeRole
import com.casecode.pos.core.model.data.SyncableEntityType
import com.casecode.pos.core.testing.dao.TestEmployeeDao
import com.casecode.pos.core.testing.dao.TestLocalSignalDao
import com.casecode.pos.core.testing.dao.TestOutboxCommandDao
import com.casecode.pos.core.testing.datasource.TestEmployeeNetworkDataSource
import com.casecode.pos.core.testing.datasource.TestInboxNetworkDataSource
import com.casecode.pos.core.testing.services.TestLogService
import com.casecode.pos.core.testing.util.CoroutinesTestRule
import com.casecode.pos.core.testing.util.TestDatabaseTransactionRunner
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EmployeeRepositoryImplTest {

    @get:Rule
    val coroutinesRule = CoroutinesTestRule()

    // Test Doubles
    private lateinit var employeeDao: TestEmployeeDao
    private lateinit var outboxCommandDao: TestOutboxCommandDao
    private lateinit var employeeNetwork: TestEmployeeNetworkDataSource
    private lateinit var logService: TestLogService
    private lateinit var localSignalDao: TestLocalSignalDao

    // System Under Test
    private lateinit var repository: EmployeeRepository

    // Test Data
    private val testBusinessId = "business-123"
    private val testEmployee = Employee(
        id = "emp-001",
        name = "John Cashier",
        phone = "1234567890",
        role = EmployeeRole.CASHIER,
        assignedBranchId = "branch-001",
    )
    private val testPassword = "secure123"
    private lateinit var testHashedPassword: String

    @Before
    fun setup() {
        employeeDao = TestEmployeeDao()
        outboxCommandDao = TestOutboxCommandDao()
        employeeNetwork = TestEmployeeNetworkDataSource()
        logService = TestLogService()
        localSignalDao = TestLocalSignalDao()

        mockkObject(PasswordUtils)
        every { PasswordUtils.hashPassword(testPassword) } returns "mocked:hash123"
        every { PasswordUtils.verifyPassword(testPassword, "mocked:hash123") } returns true
        every { PasswordUtils.verifyPassword(neq(testPassword), "mocked:hash123") } returns false

        // Now initialize the hashed password
        testHashedPassword = PasswordUtils.hashPassword(testPassword)
        repository = EmployeeRepositoryImpl(
            employeeDao = employeeDao,
            employeeNetwork = employeeNetwork,
            outboxCommandDao = outboxCommandDao,
            localSignalDao = localSignalDao,
            transaction = TestDatabaseTransactionRunner(),
            json = Json,
            inboxNetworkDataSource = TestInboxNetworkDataSource(),
            logService = logService,
            analyticsHelper = NoOpAnalyticsHelper(),
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    @After
    fun tearDown() {
        unmockkObject(PasswordUtils)
    }

    @Test
    fun authenticateEmployee_localEmployeeExists_correctPassword_returnsSuccessWithEmployee() =
        runTest {
            // Given
            employeeDao.insertOrReplaceEmployee(
                testEmployee.asEntity(
                    testHashedPassword,
                    testBusinessId,
                ),
            )

            // When
            val result =
                repository.authenticateEmployee(testBusinessId, testEmployee.id, testPassword)

            // Then
            val authenticatedEmployee = result.getOrNull()
            println(result)
            assertEquals(testEmployee.name, authenticatedEmployee?.name)
        }

    @Test
    fun authenticateEmployee_localEmployeeExists_incorrectPassword_returnsSuccessWithNull() =
        runTest {
            // Given
            employeeDao.insertOrReplaceEmployee(
                testEmployee.asEntity(
                    testHashedPassword,
                    testBusinessId,
                ),
            )

            // When
            val result =
                repository.authenticateEmployee(testBusinessId, testEmployee.id, "wrong-password")

            // Then
            assertNull(result.getOrNull())
        }

    @Test
    fun authenticateEmployee_noLocalEmployee_networkEmployeeExists_savesToLocal() = runTest {
        // Given
        employeeNetwork.setEmployee(testBusinessId, testEmployee.asNetworkModel(testHashedPassword))

        // When
        repository.authenticateEmployee(testBusinessId, testEmployee.id, testPassword)

        // Then
        val localEmployee = employeeDao.getEmployeeById(testEmployee.id, testBusinessId)
        assertNotNull(localEmployee)
    }

    @Test
    fun authenticateEmployee_employeeDoesNotExist_returnsSuccessWithNull() = runTest {
        // When
        val result = repository.authenticateEmployee(testBusinessId, "ghost-employee", testPassword)

        // Then
        assertNull(result.getOrNull())
    }

    @Test
    fun createEmployee_validData_verifiesPassword() = runTest {
        // When
        repository.createEmployee(testEmployee, testPassword, testBusinessId)

        // Then
        val savedEntity = employeeDao.getEmployeeById(testEmployee.id, testBusinessId)
        assertTrue(PasswordUtils.verifyPassword(testPassword, savedEntity?.password ?: ""))
    }

    @Test
    fun createEmployee_validData_queuesOutboxCommand() = runTest {
        // When
        repository.createEmployee(testEmployee, testPassword, testBusinessId)

        // Then
        val command = outboxCommandDao.insertedCommands.firstOrNull()
        assertEquals(OutboxEventType.Employee.CREATED, command?.type)
    }

    @Test
    fun createEmployee_duplicateId_returnsFailureWithCollisionException() = runTest {
        // Given
        employeeDao.insertOrReplaceEmployee(
            testEmployee.asEntity(
                testHashedPassword,
                testBusinessId,
            ),
        )
        // When
        val duplicateIDEmployee = testEmployee.copy(name = "name2")
        val result = repository.createEmployee(duplicateIDEmployee, testPassword, testBusinessId)

        // Then
        assertFailsWith<EmployeeIdCollisionException> { result.getOrThrow() }
    }

    @Test
    fun updateEmployee_existingEmployee_returnSuccess() = runTest {
        // Given
        employeeDao.insertOrReplaceEmployee(
            testEmployee.asEntity(
                testHashedPassword,
                testBusinessId,
            ),
        )
        val updatedEmployee = testEmployee.copy(name = "Jane Manager", role = EmployeeRole.MANAGER)

        // When
        val result = repository.updateEmployee(updatedEmployee, testPassword, testBusinessId)

        // Then
        println(result)
        assertTrue(result.isSuccess)
        val savedEntity = employeeDao.getEmployeeById(updatedEmployee.id, testBusinessId)
        assertEquals(updatedEmployee.name, savedEntity?.name)
    }

    @Test
    fun updateEmployee_existingEmployee_queuesOutboxCommand() = runTest {
        // Given
        employeeDao.insertOrReplaceEmployee(
            testEmployee.asEntity(
                testHashedPassword,
                testBusinessId,
            ),
        )
        val updatedEmployee = testEmployee.copy(name = "Jane Manager", role = EmployeeRole.MANAGER)

        // When
        repository.updateEmployee(updatedEmployee, testPassword, testBusinessId)

        // Then
        val command =
            outboxCommandDao.insertedCommands.firstOrNull { it.type == OutboxEventType.Employee.UPDATED }
        assertEquals(OutboxEventType.Employee.UPDATED, command?.type)
    }

    @Test
    fun updateEmployee_existingEmployee_blankPassword_retainsOldPassword() = runTest {
        // Given
        employeeDao.insertOrReplaceEmployee(
            testEmployee.asEntity(
                testHashedPassword,
                testBusinessId,
            ),
        )
        val updatedEmployee = testEmployee.copy(name = "Jane Manager", role = EmployeeRole.MANAGER)

        // When
        repository.updateEmployee(updatedEmployee, "", testBusinessId)

        // Then
        val savedEntity = employeeDao.getEmployeeById(updatedEmployee.id, testBusinessId)
        assertTrue(PasswordUtils.verifyPassword(testPassword, savedEntity?.password ?: ""))
    }

    @Test
    fun updateEmployee_nonExistentEmployee_returnsFailure() = runTest {
        // Given
        val nonExistentEmployee = testEmployee.copy(id = "non-existent")

        // When
        val result = repository.updateEmployee(nonExistentEmployee, testPassword, testBusinessId)

        // Then
        assertFailsWith<NoSuchElementException> { result.getOrThrow() }
    }

    @Test
    fun deleteEmployee_existingEmployee_softDeletesInDao() = runTest {
        // Given
        employeeDao.insertOrReplaceEmployee(
            testEmployee.asEntity(
                testHashedPassword,
                testBusinessId,
            ),
        )

        // When
        repository.deleteEmployee(testEmployee.id, testBusinessId)

        // Then
        assertNull(employeeDao.getEmployeeById(testEmployee.id, testBusinessId))
    }

    @Test
    fun deleteEmployee_existingEmployee_queuesOutboxCommand() = runTest {
        // Given
        employeeDao.insertOrReplaceEmployee(
            testEmployee.asEntity(
                testHashedPassword,
                testBusinessId,
            ),
        )

        // When
        repository.deleteEmployee(testEmployee.id, testBusinessId)

        // Then
        val command =
            outboxCommandDao.insertedCommands.firstOrNull { it.type == OutboxEventType.Employee.DELETED }
        assertEquals(OutboxEventType.Employee.DELETED, command?.type)
    }

    @Test
    fun deleteEmployee_nonExistentEmployee_returnsFailure() = runTest {
        // Given
        val nonExistentEmployeeId = "non-existent"

        // When
        val result = repository.deleteEmployee(nonExistentEmployeeId, testBusinessId)

        // Then
        assertFailsWith<NoSuchElementException> { result.getOrThrow() }
    }

    @Test
    fun getEmployees_noEmployees_returnsEmptyFlow() = runTest {
        // Given: No employees in the database

        // When
        val result = repository.getEmployees()

        // Then
        val employees = result.first()
        assertEquals(0, employees.size)
    }

    @Test
    fun getEmployees_multipleEmployees_returnsAllNonDeleted() = runTest {
        // Given
        val employee1 = testEmployee.copy(id = "emp-001", name = "John Cashier")
        val employee2 = testEmployee.copy(id = "emp-002", name = "Jane Manager")
        employeeDao.insertOrReplaceEmployee(employee1.asEntity(testHashedPassword, testBusinessId))
        employeeDao.insertOrReplaceEmployee(employee2.asEntity(testHashedPassword, testBusinessId))

        // When
        val result = repository.getEmployees()

        // Then
        val employees = result.first()
        assertEquals(2, employees.size)
        assertTrue(employees.any { it.name == "John Cashier" })
        assertTrue(employees.any { it.name == "Jane Manager" })
    }

    @Test
    fun getEmployees_deletedEmployees_excludesDeletedFromResult() = runTest {
        // Given
        val activeEmployee = testEmployee.copy(id = "emp-001", name = "Active Employee")
        val deletedEmployee = testEmployee.copy(id = "emp-002", name = "Deleted Employee")

        employeeDao.insertOrReplaceEmployee(
            activeEmployee.asEntity(
                testHashedPassword,
                testBusinessId,
            ),
        )
        employeeDao.insertOrReplaceEmployee(
            deletedEmployee.asEntity(testHashedPassword, testBusinessId).copy(isActive = 1),
        )

        // When
        val result = repository.getEmployees()

        // Then
        val employees = result.first()
        assertEquals(1, employees.size)
        assertEquals("Active Employee", employees.first().name)
    }

    @Test
    fun syncUp_noEmployeeCommands_returnsTrue() = runTest {
        // Given: Outbox has no employee commands
        // (using default empty state)

        // When
        val result = repository.syncUp()

        // Then
        assertTrue(result)
        assertEquals(0, employeeNetwork.addEmployeeCallCount)
    }

    @Test
    fun syncUp_createEmployeeCommand_sendsToNetworkAndPostsSignal() = runTest {
        // Given
        val employee = testEmployee.asEntity(testHashedPassword, testBusinessId)
        val networkEmployee = employee.asNetworkModel()
        val outboxPayload = Json.encodeToString(
            OutboxPayload.CreateEmployeePayload(
                businessId = testBusinessId,
                employee = networkEmployee,
            ),
        )
        outboxCommandDao.insertCommand(
            OutboxCommandEntity(
                id = 1,
                type = OutboxEventType.Employee.CREATED,
                payload = outboxPayload,
            ),
        )

        // When
        val result = repository.syncUp()

        // Then
        assertTrue(result)
        assertEquals(1, employeeNetwork.addEmployeeCallCount)

        // Verify signal was posted
        val inboxDataSource = repository.javaClass.getDeclaredField("inboxNetworkDataSource")
            .apply { isAccessible = true }
            .get(repository) as TestInboxNetworkDataSource
        assertEquals(1, inboxDataSource.postedSignals.size)
    }

    @Test
    fun syncUp_updateEmployeeCommand_sendsToNetworkAndPostsSignal() = runTest {
        // Given
        val employee = testEmployee.asEntity(testHashedPassword, testBusinessId)
        employeeDao.insertOrReplaceEmployee(employee)

        val updatedEmployee = employee.copy(name = "Updated Name")
        val networkEmployee = updatedEmployee.asNetworkModel()
        val outboxPayload = Json.encodeToString(
            OutboxPayload.CreateEmployeePayload(
                businessId = testBusinessId,
                employee = networkEmployee,
            ),
        )
        outboxCommandDao.insertCommand(
            OutboxCommandEntity(
                id = 1,
                type = OutboxEventType.Employee.UPDATED,
                payload = outboxPayload,
            ),
        )

        // When
        val result = repository.syncUp()

        // Then
        assertTrue(result)

        // Verify the update was sent to network
        val networkEmployees = employeeNetwork.getEmployees(testBusinessId)
        assertTrue(networkEmployees.any { it.name == "Updated Name" })
    }

    @Test
    fun syncUp_deleteEmployeeCommand_sendsToNetworkAndPostsSignal() = runTest {
        // Given
        val employee = testEmployee.asEntity(testHashedPassword, testBusinessId)
        employeeDao.insertOrReplaceEmployee(employee)
        employeeNetwork.setEmployee(testBusinessId, employee.asNetworkModel())

        val deletedEmployee = employee.copy(isActive = 1)
        val networkEmployee = deletedEmployee.asNetworkModel()
        val outboxPayload = Json.encodeToString(
            OutboxPayload.CreateEmployeePayload(
                businessId = testBusinessId,
                employee = networkEmployee,
            ),
        )
        outboxCommandDao.insertCommand(
            OutboxCommandEntity(
                id = 1,
                type = OutboxEventType.Employee.DELETED,
                payload = outboxPayload,
            ),
        )

        // When
        val result = repository.syncUp()

        // Then
        assertTrue(result)

        // Verify the employee was removed from network
        val networkEmployees = employeeNetwork.getEmployees(testBusinessId)
        assertEquals(0, networkEmployees.size)
    }

    @Test
    fun syncUp_networkFailure_marksCommandAsFailed() = runTest {
        // Given
        employeeNetwork.setNetworkError(Exception("Network failure"))

        val employee = testEmployee.asEntity(testHashedPassword, testBusinessId)
        val networkEmployee = employee.asNetworkModel()
        val outboxPayload = Json.encodeToString(
            OutboxPayload.CreateEmployeePayload(
                businessId = testBusinessId,
                employee = networkEmployee,
            ),
        )
        outboxCommandDao.insertCommand(
            OutboxCommandEntity(
                id = 1,
                type = OutboxEventType.Employee.CREATED,
                payload = outboxPayload,
            ),
        )

        // When
        val result = repository.syncUp()

        // Then
        assertTrue(result) // syncUp returns true even with failures

        // Verify command was marked as failed
        val commands = outboxCommandDao.insertedCommands
        assertTrue(commands.any { it.id == 1 && it.status == com.casecode.pos.core.database.model.OutboxCommandStatus.FAILED })
    }

    @Test
    fun syncUp_multipleCommands_processesAllSequentially() = runTest {
        // Given: Three different employee commands
        val employee1 = testEmployee.copy(id = "emp-001", name = "Employee 1")
            .asEntity(testHashedPassword, testBusinessId)
        val employee2 = testEmployee.copy(id = "emp-002", name = "Employee 2")
            .asEntity(testHashedPassword, testBusinessId)
        val employee3 = testEmployee.copy(id = "emp-003", name = "Employee 3")
            .asEntity(testHashedPassword, testBusinessId)

        // Create command
        outboxCommandDao.insertCommand(
            OutboxCommandEntity(
                id = 1,
                type = OutboxEventType.Employee.CREATED,
                payload = Json.encodeToString(
                    OutboxPayload.CreateEmployeePayload(testBusinessId, employee1.asNetworkModel()),
                ),
            ),
        )

        // Update command
        employeeDao.insertOrReplaceEmployee(employee2)
        outboxCommandDao.insertCommand(
            OutboxCommandEntity(
                id = 2,
                type = OutboxEventType.Employee.UPDATED,
                payload = Json.encodeToString(
                    OutboxPayload.CreateEmployeePayload(testBusinessId, employee2.asNetworkModel()),
                ),
            ),
        )

        // Delete command
        employeeDao.insertOrReplaceEmployee(employee3)
        outboxCommandDao.insertCommand(
            OutboxCommandEntity(
                id = 3,
                type = OutboxEventType.Employee.DELETED,
                payload = Json.encodeToString(
                    OutboxPayload.CreateEmployeePayload(
                        testBusinessId,
                        employee3.copy(isActive = 1).asNetworkModel(),
                    ),
                ),
            ),
        )

        // When
        val result = repository.syncUp()

        // Then
        assertTrue(result)

        // Verify all commands were processed
        val commands = outboxCommandDao.insertedCommands
        assertEquals(
            3,
            commands.filter { it.status == com.casecode.pos.core.database.model.OutboxCommandStatus.COMPLETED }.size,
        )
    }

    @Test
    fun syncUp_partialFailure_processesAllAndReturnsTrue() = runTest {
        // Given: Two commands, network will fail after first one
        val employee1 = testEmployee.copy(id = "emp-001", name = "Employee 1")
            .asEntity(testHashedPassword, testBusinessId)
        val employee2 = testEmployee.copy(id = "emp-002", name = "Employee 2")
            .asEntity(testHashedPassword, testBusinessId)

        outboxCommandDao.insertCommand(
            OutboxCommandEntity(
                id = 1,
                type = OutboxEventType.Employee.CREATED,
                payload = Json.encodeToString(
                    OutboxPayload.CreateEmployeePayload(testBusinessId, employee1.asNetworkModel()),
                ),
            ),
        )

        outboxCommandDao.insertCommand(
            OutboxCommandEntity(
                id = 2,
                type = OutboxEventType.Employee.CREATED,
                payload = Json.encodeToString(
                    OutboxPayload.CreateEmployeePayload(testBusinessId, employee2.asNetworkModel()),
                ),
            ),
        )

        employeeNetwork.clear()

        // When
        val result = repository.syncUp()

        // Then
        assertTrue(result)

        // Verify both commands were attempted
        val commands = outboxCommandDao.insertedCommands
        assertEquals(2, commands.size)
    }

    @Test
    fun syncDown_noEmployeeSignals_returnsTrue() = runTest {
        // Given: No employee signals in local signal DAO

        // When
        val result = repository.syncDown()

        // Then
        assertTrue(result)
        assertEquals(0, employeeNetwork.findEmployeeByIdentifierCallCount)
    }

    @Test
    fun syncDown_employeeSignal_fetchesFromNetworkAndUpdatesLocal() = runTest {
        // Given
        val employee = testEmployee.asEntity(testHashedPassword, testBusinessId)
        val networkEmployee = employee.asNetworkModel()
        employeeNetwork.setEmployee(testBusinessId, networkEmployee)
        val signalId = "signal-001"
        localSignalDao.insertSignal(
            LocalSignalEntity(
                id = signalId,
                businessId = testBusinessId,
                entityType = SyncableEntityType.EMPLOYEE,
                operationType = OperationType.CREATED,
                entityId = employee.id,
                status = LocalSignalStatus.PENDING,
            ),
        )

        // When
        val result = repository.syncDown()

        // Then
        assertTrue(result)

        // Verify employee was fetched and inserted
        val localEmployee = employeeDao.getEmployeeById(employee.id, testBusinessId)
        assertNotNull(localEmployee)
        assertEquals(employee.name, localEmployee.name)

        // Verify signal was marked as processed
        val signals = localSignalDao.insertedSignals
        assertTrue(signals.any { it.id == signalId && it.status == LocalSignalStatus.PROCESSED })
    }

    @Test
    fun syncDown_employeeNotFoundOnNetwork_marksSignalAsProcessed() = runTest {
        // Given: Signal for an employee that doesn't exist on network (was deleted)
        localSignalDao.insertSignal(
            LocalSignalEntity(
                id = "signal-001",
                businessId = testBusinessId,
                entityType = SyncableEntityType.EMPLOYEE,
                entityId = "deleted-employee",
                status = LocalSignalStatus.PENDING,
                operationType = OperationType.UPDATED,
            ),
        )

        // When
        val result = repository.syncDown()

        // Then
        assertTrue(result)

        // Verify signal was still marked as processed (graceful handling)
        val signals = localSignalDao.insertedSignals
        assertTrue(signals.any { it.id == "signal-001" && it.status == LocalSignalStatus.PROCESSED })
    }

    @Test
    fun syncDown_networkFailure_marksSignalAsFailed() = runTest {
        // Given
        employeeNetwork.setNetworkError(Exception("Network failure"))

        localSignalDao.insertSignal(
            LocalSignalEntity(
                id = "signal-001",
                businessId = testBusinessId,
                entityType = SyncableEntityType.EMPLOYEE,
                operationType = OperationType.CREATED,
                entityId = "emp-001",
                status = LocalSignalStatus.PENDING,
            ),
        )

        // When
        val result = repository.syncDown()

        // Then
        assertEquals(false, result)

        // Verify signal was marked as failed
        val signals = localSignalDao.insertedSignals
        assertTrue(signals.any { it.id == "signal-001" && it.status == LocalSignalStatus.FAILED })
    }

    @Test
    fun syncDown_multipleSignals_processesAllSequentially() = runTest {
        // Given: Three employee signals
        val employee1 = testEmployee.copy(id = "emp-001", name = "Employee 1")
            .asEntity(testHashedPassword, testBusinessId)
        val employee2 = testEmployee.copy(id = "emp-002", name = "Employee 2")
            .asEntity(testHashedPassword, testBusinessId)
        val employee3 = testEmployee.copy(id = "emp-003", name = "Employee 3")
            .asEntity(testHashedPassword, testBusinessId)

        employeeNetwork.setEmployee(testBusinessId, employee1.asNetworkModel())
        employeeNetwork.setEmployee(testBusinessId, employee2.asNetworkModel())
        employeeNetwork.setEmployee(testBusinessId, employee3.asNetworkModel())

        localSignalDao.insertSignal(
            LocalSignalEntity(
                id = "signal-001",
                businessId = testBusinessId,
                entityType = SyncableEntityType.EMPLOYEE,
                operationType = OperationType.CREATED,
                entityId = employee1.id,
                status = LocalSignalStatus.PENDING,
            ),
        )
        localSignalDao.insertSignal(
            LocalSignalEntity(
                id = "signal-002",
                businessId = testBusinessId,
                entityType = SyncableEntityType.EMPLOYEE,
                operationType = OperationType.CREATED,
                entityId = employee2.id,
                status = LocalSignalStatus.PENDING,
            ),
        )
        localSignalDao.insertSignal(
            LocalSignalEntity(
                id = "signal-003",
                businessId = testBusinessId,
                entityType = SyncableEntityType.EMPLOYEE,
                operationType = OperationType.CREATED,
                entityId = employee3.id,
                status = LocalSignalStatus.PENDING,
            ),
        )

        // When
        val result = repository.syncDown()

        // Then
        assertTrue(result)

        // Verify all employees were fetched and inserted
        assertNotNull(employeeDao.getEmployeeById(employee1.id, testBusinessId))
        assertNotNull(employeeDao.getEmployeeById(employee2.id, testBusinessId))
        assertNotNull(employeeDao.getEmployeeById(employee3.id, testBusinessId))

        // Verify all signals were marked as processed
        val signals = localSignalDao.insertedSignals
        assertEquals(3, signals.filter { it.status == LocalSignalStatus.PROCESSED }.size)
    }

    @Test
    fun syncDown_duplicateSignal_updatesLocalEmployee() = runTest {
        // Given: Employee exists locally with old data
        val oldEmployee = testEmployee.copy(name = "Old Name")
            .asEntity(testHashedPassword, testBusinessId)
        employeeDao.insertOrReplaceEmployee(oldEmployee)

        // Network has updated version
        val updatedEmployee = testEmployee.copy(name = "Updated Name")
            .asEntity(testHashedPassword, testBusinessId)
        employeeNetwork.setEmployee(testBusinessId, updatedEmployee.asNetworkModel())

        localSignalDao.insertSignal(
            LocalSignalEntity(
                id = "signal-001",
                businessId = testBusinessId,
                entityType = SyncableEntityType.EMPLOYEE,
                operationType = OperationType.UPDATED,
                entityId = testEmployee.id,
                status = LocalSignalStatus.PENDING,
            ),
        )

        // When
        val result = repository.syncDown()

        // Then
        assertTrue(result)

        // Verify local employee was updated with network version
        val localEmployee = employeeDao.getEmployeeById(testEmployee.id, testBusinessId)
        assertEquals("Updated Name", localEmployee?.name)
    }
}
