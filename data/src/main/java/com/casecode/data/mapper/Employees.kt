package com.casecode.data.mapper

import com.casecode.domain.model.users.Employee
import com.casecode.domain.utils.EMPLOYEES_FIELD
import com.casecode.domain.utils.EMPLOYEE_BRANCH_NAME_FIELD
import com.casecode.domain.utils.EMPLOYEE_NAME_FIELD
import com.casecode.domain.utils.EMPLOYEE_PASSWORD_FIELD
import com.casecode.domain.utils.EMPLOYEE_PERMISSION_FIELD
import com.casecode.domain.utils.EMPLOYEE_PHONE_NUMBER_FIELD
fun List<Employee>.isEmployeeNameDuplicate(
    employee: Employee,
    oldEmployee: Employee? = null,
): Boolean {
    this.forEach {
        if (it.name == employee.name && it != oldEmployee) {
            return true
        }
    }
    return false
}
/**
 * Created by Mahmoud Abdalhafeez
 */
fun List<Employee>.asExternalEmployees(): HashMap<String, MutableList<Map<String, Any?>>> {
    val employeesRequest = mutableListOf<Map<String, Any?>>()
    this.forEach {
        val employeeData =
            hashMapOf(
                EMPLOYEE_NAME_FIELD to it.name,
                EMPLOYEE_PHONE_NUMBER_FIELD to it.phoneNumber,
                EMPLOYEE_PASSWORD_FIELD to it.password,
                EMPLOYEE_BRANCH_NAME_FIELD to it.branchName,
                EMPLOYEE_PERMISSION_FIELD to it.permission,
            )
        employeesRequest.add(employeeData)
    }
    return hashMapOf(EMPLOYEES_FIELD to employeesRequest)
}
fun Employee.asExternalEmployee(): Map<String, Any?> {

        return hashMapOf(
                EMPLOYEE_NAME_FIELD to this.name,
                EMPLOYEE_PHONE_NUMBER_FIELD to this.phoneNumber,
                EMPLOYEE_PASSWORD_FIELD to this.password,
                EMPLOYEE_BRANCH_NAME_FIELD to this.branchName,
                EMPLOYEE_PERMISSION_FIELD to this.permission,
            )

}
fun List<Map<String, Any>>.asEntityEmployees(): List<Employee>{
    val employees = mutableListOf<Employee>()
    this.forEach {
        val employee = Employee(
                name = it[EMPLOYEE_NAME_FIELD] as String,
                phoneNumber = it[EMPLOYEE_PHONE_NUMBER_FIELD] as String,
                password = it[EMPLOYEE_PASSWORD_FIELD] as String,
                branchName = it[EMPLOYEE_BRANCH_NAME_FIELD] as String,
                permission = it[EMPLOYEE_PERMISSION_FIELD] as String
                )
        employees.add(employee)
    }
    return employees
}