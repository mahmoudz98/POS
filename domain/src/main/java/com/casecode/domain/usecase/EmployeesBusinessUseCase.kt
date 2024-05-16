package com.casecode.domain.usecase

import com.casecode.domain.model.users.Employee
import com.casecode.domain.repository.AddEmployees
import com.casecode.domain.repository.EmployeesBusinessRepository
import com.casecode.domain.utils.EmptyType
import com.casecode.domain.utils.Resource
import com.casecode.pos.domain.R
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetEmployeesBusinessUseCase @Inject constructor(private val employeesRepo: EmployeesBusinessRepository) {
    suspend operator fun invoke(employees: MutableList<Employee>, uid: String): AddEmployees {
        if (uid.isEmpty()) {
            return Resource.empty(EmptyType.DATA, R.string.uid_empty)
        }
        if (employees.isEmpty()) {
            return Resource.empty(EmptyType.DATA, R.string.employees_empty)
        }
        return employeesRepo.setEmployees(employees, uid)
    }
}

class GetEmployeesBusinessUseCase @Inject constructor(private val employeesRepo: EmployeesBusinessRepository) {
    operator fun invoke(): Flow<Resource<List<Employee>>> =
        employeesRepo.getEmployees()
}

class AddEmployeesUseCase @Inject constructor(private val employeesRepo: EmployeesBusinessRepository) {
    suspend operator fun invoke(employee: Employee): Resource<Boolean> {
        return employeesRepo.addEmployee(employee)
    }
}

class UpdateEmployeesUseCase @Inject constructor(private val employeesRepo: EmployeesBusinessRepository) {
    suspend operator fun invoke(employee: Employee, oldEmployee: Employee): Resource<Boolean> {
        return employeesRepo.updateEmployee(employee, oldEmployee)
    }
}