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
package com.casecode.pos.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.casecode.pos.core.model.data.business.Employee

@Entity(tableName = "employee")
data class EmployeeEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "employee_id") val employeeId: String,
    @ColumnInfo(name = "business_id") val businessId: String,
    val name: String,
    val role: String,
    @ColumnInfo(name = "assigned_branch_ids") val assignedBranchIds: List<String>,
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false,
)

fun EmployeeEntity.asExternalModel(): Employee {
    return Employee(
        id = this.id,
        employeeId = this.employeeId,
        businessId = this.businessId,
        name = this.name,
        role = this.role,
        assignedBranchIds = this.assignedBranchIds,
    )
}

fun Employee.asEntity(): EmployeeEntity {
    return EmployeeEntity(
        id = this.id,
        employeeId = this.employeeId,
        businessId = this.businessId,
        name = this.name,
        role = this.role,
        assignedBranchIds = this.assignedBranchIds,
    )
}