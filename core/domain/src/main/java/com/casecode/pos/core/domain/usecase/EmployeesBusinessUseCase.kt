package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.R
import com.casecode.pos.core.domain.repository.AddEmployees
import com.casecode.pos.core.domain.repository.EmployeesBusinessRepository
import com.casecode.pos.core.domain.utils.EmptyType
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Employee
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetEmployeesBusinessUseCase @Inject constructor(private val employeesRepo: EmployeesBusinessRepository) {
    suspend operator fun invoke(employees: MutableList<Employee>): AddEmployees {

        if (employees.isEmpty()) {
            return Resource.empty(EmptyType.DATA, R.string.employees_empty)
        }
        return employeesRepo.setEmployees(employees)
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