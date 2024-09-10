package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Employee
import com.casecode.pos.core.testing.repository.TestEmployeesBusinessRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeleteEmployeeUseCaseTest {
    // Subject under test
    private val testEmployeesBusinessRepository: TestEmployeesBusinessRepository =
        TestEmployeesBusinessRepository()
    private val deleteEmployeeUseCase =
        DeleteEmployeeUseCase(testEmployeesBusinessRepository)

    @Test
    fun deleteEmployees_returnResourceSuccess() =
        runTest {
            val employees = Employee()
            // When
            val resultAddEmployeesBusiness = deleteEmployeeUseCase(employees)

            // Then
            assert(resultAddEmployeesBusiness is Resource.Success)
        }

    @Test
    fun deleteEmployee_whenHasError_returnResourceError() = runTest {
        testEmployeesBusinessRepository.setReturnError(true)
        val employees = Employee()
        // When
        val resultAddEmployeesBusiness = deleteEmployeeUseCase(employees)
        // Then
        assert(resultAddEmployeesBusiness is Resource.Error)

    }
}