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
import androidx.room.Index
import androidx.room.PrimaryKey
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.model.business.EmployeeRole

@Entity(tableName = "employees", indices = [Index(value = ["id", "business_id"], unique = true)])
data class EmployeeEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val password: String,
    val phone: String,
    val role: Int,
    @ColumnInfo(name = "business_id") val businessId: String,
    @ColumnInfo(name = "assigned_branch_id") val assignedBranchId: String,
    @ColumnInfo(name = "is_active") val isActive: Int = 0,
)

fun EmployeeEntity.asExternalModel(): Employee {
    return Employee(
        id = this.id,
        name = this.name,
        phone = this.phone,
        role = EmployeeRole.fromInt(this.role),
        assignedBranchId = this.assignedBranchId,
    )
}

fun Employee.asEntity(hashedPassword: String, businessId: String): EmployeeEntity {
    return EmployeeEntity(
        id = this.id,
        name = this.name,
        role = this.role.ordinal,
        password = hashedPassword,
        phone = this.phone,
        businessId = businessId,
        assignedBranchId = this.assignedBranchId,
    )
}
