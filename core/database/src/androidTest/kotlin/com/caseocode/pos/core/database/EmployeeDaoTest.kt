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
package com.caseocode.pos.core.database

import app.cash.turbine.test
import com.casecode.pos.core.database.model.EmployeeEntity
import com.casecode.pos.core.model.business.EmployeeRole
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class EmployeeDaoTest : DatabaseTest() {

    @Test
    fun insertAndGetEmployee_matchesInsertedData() = runTest {
        val employee = createEmployee(id = "101", businessId = "CMP_A", name = "Alice")

        employeeDao.insertOrReplaceEmployee(employee)
        val loaded = employeeDao.getEmployeeById("101", "CMP_A")

        assertNotNull(loaded)
        assertEquals("101", loaded.id)
        assertEquals("Alice", loaded.name)
        assertEquals("CMP_A", loaded.businessId)
        assertEquals(0, loaded.isActive)
    }

    @Test
    fun insertEmployee_withSpecialCharacters_persistsCorrectly() = runTest {
        val employee = createEmployee(
            id = "101",
            name = "O'Brien-Smith",
            phone = "+20-123-456-7890",
        )

        employeeDao.insertOrReplaceEmployee(employee)
        val loaded = employeeDao.getEmployeeById("101", "CMP_A")

        assertNotNull(loaded)
        assertEquals("O'Brien-Smith", loaded.name)
        assertEquals("+20-123-456-7890", loaded.phone)
    }

    @Test
    fun insertEmployee_withVeryLongId_persistsCorrectly() = runTest {
        val longId = "E".repeat(100)
        val employee = createEmployee(id = longId)

        employeeDao.insertOrReplaceEmployee(employee)
        val loaded = employeeDao.getEmployeeById(longId, "CMP_A")

        assertNotNull(loaded)
        assertEquals(longId, loaded.id)
    }

    @Test
    fun getEmployeeById_nonExistent_returnsNull() = runTest {
        val loaded = employeeDao.getEmployeeById("999", "CMP_A")

        assertNull(loaded)
    }

    @Test
    fun getEmployeeById_wrongBusinessId_returnsNull() = runTest {
        val employee = createEmployee(id = "101", businessId = "CMP_A")
        employeeDao.insertOrReplaceEmployee(employee)

        val loaded = employeeDao.getEmployeeById("101", "CMP_B")

        assertNull(loaded)
    }

    @Test
    fun getEmployees_returnsAllEmployees_acrossBusinesses() = runTest {
        employeeDao.insertOrReplaceEmployee(createEmployee(id = "1", businessId = "CMP_A"))
        employeeDao.insertOrReplaceEmployee(createEmployee(id = "2", businessId = "CMP_B"))
        employeeDao.insertOrReplaceEmployee(createEmployee(id = "3", businessId = "CMP_A"))

        val employees = employeeDao.getEmployees().first()

        assertEquals(3, employees.size)
    }

    @Test
    fun getEmployees_includesInactiveEmployees() = runTest {
        employeeDao.insertOrReplaceEmployee(createEmployee(id = "1", businessId = "CMP_A"))
        employeeDao.deleteEmployee("1", "CMP_A")

        val employees = employeeDao.getEmployees().first()

        assertTrue(employees.isEmpty())
    }

    @Test
    fun updateEmployee_persistsAllChanges() = runTest {
        val original = createEmployee(
            id = "200",
            name = "Old Name",
            phone = "111",
            role = EmployeeRole.CASHIER,
        )
        employeeDao.insertOrReplaceEmployee(original)

        val updated = original.copy(
            name = "New Name",
            phone = "555-0000",
            role = EmployeeRole.MANAGER.ordinal,
            assignedBranchId = "BR_2",
        )
        employeeDao.updateEmployees(updated)

        val loaded = employeeDao.getEmployeeById("200", "CMP_A")
        assertNotNull(loaded)
        assertEquals("New Name", loaded.name)
        assertEquals("555-0000", loaded.phone)
        assertEquals(EmployeeRole.MANAGER.ordinal, loaded.role)
        assertEquals("BR_2", loaded.assignedBranchId)
    }

    @Test
    fun updateEmployees_multipleEmployees_persistsAll() = runTest {
        val emp1 = createEmployee(id = "1", name = "Employee 1")
        val emp2 = createEmployee(id = "2", name = "Employee 2")
        employeeDao.insertOrReplaceEmployee(emp1)
        employeeDao.insertOrReplaceEmployee(emp2)

        val updated1 = emp1.copy(name = "Updated 1")
        val updated2 = emp2.copy(name = "Updated 2")
        employeeDao.updateEmployees(updated1, updated2)

        val loaded1 = employeeDao.getEmployeeById("1", "CMP_A")
        val loaded2 = employeeDao.getEmployeeById("2", "CMP_A")
        assertEquals("Updated 1", loaded1?.name)
        assertEquals("Updated 2", loaded2?.name)
    }

    @Test
    fun updateEmployee_nonExistent_doesNotThrowException() = runTest {
        val nonExistent = createEmployee(id = "999")

        employeeDao.updateEmployees(nonExistent)
    }

    @Test
    fun deleteEmployee_performsSoftDelete_setsIsActiveTo1() = runTest {
        val emp = createEmployee(id = "300", businessId = "CMP_A")
        employeeDao.insertOrReplaceEmployee(emp)

        employeeDao.deleteEmployee("300", "CMP_A")

        val loaded = employeeDao.getEmployeeById("300", "CMP_A")
        assertNotNull(loaded)
        assertEquals(1, loaded.isActive)
    }

    @Test
    fun deleteEmployee_nonExistent_doesNothing() = runTest {
        employeeDao.deleteEmployee("999", "CMP_A")

        val loaded = employeeDao.getEmployeeById("999", "CMP_A")
        assertNull(loaded)
    }

    @Test
    fun deleteEmployee_wrongBusinessId_doesNotAffectEmployee() = runTest {
        val emp = createEmployee(id = "300", businessId = "CMP_A")
        employeeDao.insertOrReplaceEmployee(emp)

        employeeDao.deleteEmployee("300", "CMP_B")

        val loaded = employeeDao.getEmployeeById("300", "CMP_A")
        assertNotNull(loaded)
        assertEquals(0, loaded.isActive)
    }

    @Test
    fun deleteEmployee_alreadyDeleted_remainsDeleted() = runTest {
        val emp = createEmployee(id = "300")
        employeeDao.insertOrReplaceEmployee(emp)
        employeeDao.deleteEmployee("300", "CMP_A")

        employeeDao.deleteEmployee("300", "CMP_A")

        val loaded = employeeDao.getEmployeeById("300", "CMP_A")
        assertNotNull(loaded)
        assertEquals(1, loaded.isActive)
    }

    @Test
    fun getHighestEmployeeId_returnsMaxNumericId_scopedToBusiness() = runTest {
        employeeDao.insertOrReplaceEmployee(createEmployee(id = "10", businessId = "CMP_A"))
        employeeDao.insertOrReplaceEmployee(createEmployee(id = "50", businessId = "CMP_A"))
        employeeDao.insertOrReplaceEmployee(createEmployee(id = "5", businessId = "CMP_A"))
        employeeDao.insertOrReplaceEmployee(createEmployee(id = "100", businessId = "CMP_B"))

        val maxId = employeeDao.getHighestEmployeeId()

        assertEquals(100, maxId)
    }

    @Test
    fun getHighestEmployeeId_returnsNull_ifTableEmpty() = runTest {
        val maxId = employeeDao.getHighestEmployeeId()

        assertNull(maxId)
    }

    @Test
    fun getHighestEmployeeId_whenEmployeesAreEmpty_returnsNull() = runTest {

        val maxId = employeeDao.getHighestEmployeeId()

        assertNull(maxId)
    }

    @Test
    fun getHighestEmployeeId_ignoresNonNumericIds() = runTest {
        employeeDao.insertOrReplaceEmployee(createEmployee(id = "10", businessId = "CMP_A"))
        employeeDao.insertOrReplaceEmployee(createEmployee(id = "ABC", businessId = "CMP_A"))
        employeeDao.insertOrReplaceEmployee(createEmployee(id = "50", businessId = "CMP_A"))

        val maxId = employeeDao.getHighestEmployeeId()

        assertEquals(50, maxId)
    }

    @Test
    fun getHighestEmployeeId_includesDeletedEmployees() = runTest {
        employeeDao.insertOrReplaceEmployee(createEmployee(id = "100", businessId = "CMP_A"))
        employeeDao.deleteEmployee("100", "CMP_A")
        employeeDao.insertOrReplaceEmployee(createEmployee(id = "50", businessId = "CMP_A"))

        val maxId = employeeDao.getHighestEmployeeId()

        assertEquals(100, maxId)
    }

    @Test
    fun getEmployees_emitsInitialList() = runTest {
        val emp1 = createEmployee(id = "1", name = "First")
        employeeDao.insertOrReplaceEmployee(emp1)

        employeeDao.getEmployees().test {
            val emission = awaitItem()
            assertEquals(1, emission.size)
            assertEquals("First", emission[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getEmployees_emitsUpdates_whenDataInserted() = runTest {
        val emp1 = createEmployee(id = "1", name = "First")

        employeeDao.getEmployees().test {
            assertEquals(0, awaitItem().size)

            employeeDao.insertOrReplaceEmployee(emp1)

            val emission = awaitItem()
            assertEquals(1, emission.size)
            assertEquals("First", emission[0].name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getEmployees_emitsUpdates_whenDataUpdated() = runTest {
        val emp = createEmployee(id = "1", name = "Original")
        employeeDao.insertOrReplaceEmployee(emp)

        employeeDao.getEmployees().test {
            awaitItem() // Initial emission

            employeeDao.updateEmployees(emp.copy(name = "Updated"))

            val emission = awaitItem()
            assertEquals("Updated", emission[0].name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getEmployees_emitsUpdates_whenDataDeleted() = runTest {
        val emp = createEmployee(id = "1")
        employeeDao.insertOrReplaceEmployee(emp)

        employeeDao.getEmployees().test {
            val initial = awaitItem()
            assertEquals(0, initial[0].isActive)

            employeeDao.deleteEmployee("1", "CMP_A")

            val afterDelete = awaitItem()
            assertTrue(afterDelete.isEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertEmployee_withAllRoles_persistsCorrectly() = runTest {
        EmployeeRole.entries.forEachIndexed { index, role ->
            val emp = createEmployee(
                id = "ROLE_$index",
                role = role,
            )
            employeeDao.insertOrReplaceEmployee(emp)

            val loaded = employeeDao.getEmployeeById("ROLE_$index", "CMP_A")
            assertNotNull(loaded)
            assertEquals(role.ordinal, loaded.role)
        }
    }

    @Test
    fun getEmployees_emptyDatabase_returnsEmptyList() = runTest {
        val employees = employeeDao.getEmployees().first()

        assertTrue(employees.isEmpty())
    }

    private fun createEmployee(
        id: String,
        businessId: String = "CMP_A",
        name: String = "Test User",
        phone: String = "123",
        branchId: String = "BR_1",
        role: EmployeeRole = EmployeeRole.CASHIER,
    ): EmployeeEntity {
        return EmployeeEntity(
            id = id,
            name = name,
            password = "hashed_password_123",
            phone = phone,
            role = role.ordinal,
            businessId = businessId,
            assignedBranchId = branchId,
            isActive = 0,
        )
    }
}
