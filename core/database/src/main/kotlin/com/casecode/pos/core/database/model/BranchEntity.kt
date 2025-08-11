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
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.BranchStatus
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(tableName = "branch")
data class BranchEntity(
    @PrimaryKey
    @ColumnInfo(name = "branch_id") val branchId: String = Uuid.random().toString(),
    val name: String,
    val phone: String,
    val status: Int,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant,
)

fun BranchEntity.asExternalModel(): Branch {
    return Branch(
        id = this.branchId,
        name = this.name,
        phone = this.phone,
        status = BranchStatus.fromValue(this.status),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}

fun Branch.asEntity(): BranchEntity {
    return BranchEntity(
        name = this.name,
        phone = this.phone,
        status = this.status.value,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}