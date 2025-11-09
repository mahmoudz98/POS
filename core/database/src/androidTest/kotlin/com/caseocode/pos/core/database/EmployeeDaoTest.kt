package com.caseocode.pos.core.database

import com.casecode.pos.core.database.model.EmployeeEntity
import com.casecode.pos.core.model.business.EmployeeRole
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class EmployeeDaoTest : DatabaseTest() {


    @Test
    fun getEmployees() = runTest {
        insertEmployees()
        val savedEmployees = employeeDao.getEmployees().first()
        assertEquals(
            employeeEntities.map { it.name },
            savedEmployees.map { it.name },
        )
    }

    @Test
    fun getEmployeeByName() = runTest {
        insertEmployees()
        val savedEmployee = employeeDao.getEmployeeByName(employeeEntities[0].name)
        assertEquals(
            employeeEntities[0].name,
            savedEmployee?.name,
        )
    }

    @Test
    fun insertEmployee_newEntryIsIgnoredIfNameIsAlreadyExists() = runTest {
        insertEmployees()
        val newEmployee = EmployeeEntity(
            name = employeeEntities[0].name,
            phone = "1",
            role = EmployeeRole.CASHIER.ordinal,
            assignedBranchId = "branch1",
            password = "hashed_password",
        )
        employeeDao.insertOrReplaceEmployee(newEmployee)
        val savedEmployee = employeeDao.getEmployees().first()
        assertEquals(3, savedEmployee.size)

    }

    @Test
    fun updateEmployee_updatesExistingEntry() = runTest {
        insertEmployees()
        val updatedEmployee = employeeEntities[0].copy(name = "Updated Name")
        employeeDao.updateEmployees(updatedEmployee)
        val savedEmployee = employeeDao.getEmployees().first()

        assertEquals(
            updatedEmployee,
            savedEmployee[0],
        )
    }

    @Test
    fun deleteEmployee_deletesExistingEntry() = runTest {
        insertEmployees()
        val employeeToDelete = employeeEntities[0]
        employeeDao.deleteEmployee(employeeToDelete.id)
        val savedEmployees = employeeDao.getEmployees().first()
        assertEquals(2, savedEmployees.size,)
    }

    private suspend fun insertEmployees() {
        employeeDao.insertOrReplaceEmployee(employeeEntities[0])
        employeeDao.insertOrReplaceEmployee(employeeEntities[1])
        employeeDao.insertOrReplaceEmployee(employeeEntities[2])

    }

    private val employeeEntities = listOf(
        EmployeeEntity(
            name = "John Doe",
            phone = "1234567890",
            role = EmployeeRole.CASHIER.ordinal,
            assignedBranchId = "branch1",
            password = "hashed_password",
        ),
        EmployeeEntity(
            name = "Jane Smith",
            phone = "912312312",
            role = EmployeeRole.CASHIER.ordinal,
            assignedBranchId = "branch2",
            password = "hashed_password",
        ),
        EmployeeEntity(
            name = "Bob Johnson",
            phone = "1123432432",
            role = EmployeeRole.MANAGER.ordinal,
            assignedBranchId = "branch3",
            password = "hashed_password",
        ),
    )
}
