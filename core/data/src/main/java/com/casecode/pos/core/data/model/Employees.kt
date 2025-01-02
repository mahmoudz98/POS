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
package com.casecode.pos.core.data.model

import com.casecode.pos.core.firebase.services.EMPLOYEES_FIELD
import com.casecode.pos.core.firebase.services.EMPLOYEE_BRANCH_NAME_FIELD
import com.casecode.pos.core.firebase.services.EMPLOYEE_NAME_FIELD
import com.casecode.pos.core.firebase.services.EMPLOYEE_PASSWORD_FIELD
import com.casecode.pos.core.firebase.services.EMPLOYEE_PERMISSION_FIELD
import com.casecode.pos.core.firebase.services.EMPLOYEE_PHONE_NUMBER_FIELD
import com.casecode.pos.core.model.data.users.Employee

fun Map<String, Any>.asExternalModel(): Employee = Employee(
    name = this[EMPLOYEE_NAME_FIELD] as String,
    phoneNumber = this[EMPLOYEE_PHONE_NUMBER_FIELD] as String,
    password = this[EMPLOYEE_PASSWORD_FIELD] as String,
    branchName = this[EMPLOYEE_BRANCH_NAME_FIELD] as String,
    permission = this[EMPLOYEE_PERMISSION_FIELD] as String,
)

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

fun Employee.asExternalEmployee(): Map<String, Any?> = hashMapOf(
    EMPLOYEE_NAME_FIELD to this.name,
    EMPLOYEE_PHONE_NUMBER_FIELD to this.phoneNumber,
    EMPLOYEE_PASSWORD_FIELD to this.password,
    EMPLOYEE_BRANCH_NAME_FIELD to this.branchName,
    EMPLOYEE_PERMISSION_FIELD to this.permission,
)

fun List<Map<String, Any>>.asEntityEmployees(): List<Employee> {
    val employees = mutableListOf<Employee>()
    this.forEach {
        val employee =
            Employee(
                name = it[EMPLOYEE_NAME_FIELD] as String,
                phoneNumber = it[EMPLOYEE_PHONE_NUMBER_FIELD] as String,
                password = it[EMPLOYEE_PASSWORD_FIELD] as String,
                branchName = it[EMPLOYEE_BRANCH_NAME_FIELD] as String,
                permission = it[EMPLOYEE_PERMISSION_FIELD] as String,
            )
        employees.add(employee)
    }
    return employees
}