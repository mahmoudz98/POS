package com.casecode.pos.core.testing.repository

import com.casecode.pos.core.model.data.users.Employee
import com.casecode.pos.core.domain.repository.AddEmployees
import com.casecode.pos.core.domain.repository.EmployeesBusinessRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.data.R
import com.casecode.pos.core.testing.base.BaseTestRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Rule
import javax.inject.Inject

class TestEmployeesBusinessRepository @Inject constructor() : EmployeesBusinessRepository, BaseTestRepository() {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    override fun init() {
    }

    val fakeEmployees = listOf(
        Employee("Mahmoud", "1018867266", "123213", "branch1", "Sale"),
        Employee("Ahmed", "22323", "123231213", "branch2", "Admin"),
    )

    override fun getEmployees(): Flow<Resource<List<Employee>>> {
        return flow<Resource<List<Employee>>> {
            if (shouldReturnError) return@flow emit(Resource.error(R.string.core_data_get_business_failure))
            if (shouldReturnEmpty) return@flow emit(Resource.empty())
            emit(Resource.success(fakeEmployees))
        }
    }

    override suspend fun setEmployees(employees: MutableList<Employee>): AddEmployees {
        if (shouldReturnError) {
            return Resource.Error("Exception")
        } else if (shouldReturnEmpty) {
            return Resource.Empty()
        }
        return Resource.Success(true)
    }

    override suspend fun addEmployee(employees: Employee): Resource<Boolean> {
        if (shouldReturnError) {
            print("error")
            return Resource.error(R.string.core_data_add_employees_business_failure)
        }
        return Resource.success(true)
    }

    override suspend fun updateEmployee(
        employees: Employee,
        oldEmployee: Employee,
    ): Resource<Boolean> {
        if (shouldReturnError) return Resource.error(R.string.core_data_employee_update_business_failure)
        return Resource.success(true)
    }

}