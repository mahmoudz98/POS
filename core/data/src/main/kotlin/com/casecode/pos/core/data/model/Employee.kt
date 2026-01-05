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

import com.casecode.pos.core.database.model.EmployeeEntity
import com.casecode.pos.core.firebase.model.NetworkEmployee
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.model.business.EmployeeRole

fun NetworkEmployee.asExternalModel(): Employee = Employee(
    id = this.id,
    name = this.name,
    phone = phone,
    role = EmployeeRole.fromInt(this.role),
    assignedBranchId = this.assignedBranchIds,

)
fun NetworkEmployee.asEntity(businessId: String): EmployeeEntity = EmployeeEntity(
    id = this.id,
    name = this.name,
    phone = phone,
    role = this.role,
    password = this.password,
    businessId = businessId,
    assignedBranchId = this.assignedBranchIds,
)
fun EmployeeEntity.asNetworkModel(): NetworkEmployee = NetworkEmployee(
    id = this.id,
    name = this.name,
    role = this.role,
    phone = this.phone,
    assignedBranchIds = this.assignedBranchId,
    password = this.password,
)

fun Employee.asNetworkModel(hashedPassword: String): NetworkEmployee = NetworkEmployee(
    id = this.id,
    name = this.name,
    role = this.role.ordinal,
    phone = this.phone,
    assignedBranchIds = this.assignedBranchId,
    password = hashedPassword,
)
