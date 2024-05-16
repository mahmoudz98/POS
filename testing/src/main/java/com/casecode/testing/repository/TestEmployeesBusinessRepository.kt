package com.casecode.testing.repository

import com.casecode.domain.model.users.Employee
import com.casecode.domain.repository.AddEmployees
import com.casecode.domain.repository.EmployeesBusinessRepository
import com.casecode.domain.utils.Resource
import com.casecode.pos.data.R
import com.casecode.testing.base.BaseTestRepository
import com.casecode.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Rule
import javax.inject.Inject

class TestEmployeesBusinessRepository @Inject constructor() : EmployeesBusinessRepository,
    BaseTestRepository() {
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
            if (shouldReturnError) return@flow emit(Resource.error(R.string.get_business_failure))
            if (shouldReturnEmpty) return@flow emit(Resource.empty())
            emit(Resource.success(fakeEmployees))
        }
    }

    override suspend fun setEmployees(employees: MutableList<Employee>, uid: String): AddEmployees {
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
            return Resource.error(R.string.add_employees_business_failure)
        }
        return Resource.success(true)
    }

    override suspend fun updateEmployee(
        employees: Employee,
        oldEmployee: Employee,
    ): Resource<Boolean> {
        if (shouldReturnError) return Resource.error(R.string.employee_update_business_failure)
        return Resource.success(true)
    }

}