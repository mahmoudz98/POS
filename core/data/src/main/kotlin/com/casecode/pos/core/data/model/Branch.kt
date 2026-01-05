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

import com.casecode.pos.core.data.utils.toFirestoreTimestamp
import com.casecode.pos.core.data.utils.toKotlinInstant
import com.casecode.pos.core.database.model.BranchEntity
import com.casecode.pos.core.firebase.model.NetworkBranch
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.BranchStatus

/**
 * Converts a [NetworkBranch] data transfer object to a [Branch] domain model.
 */
fun NetworkBranch.asExternalModel(): Branch = Branch(
    id = this.id,
    name = this.name,
    phone = this.phone,
    status = this.status,
    createdAt = this.createdAt.toKotlinInstant(),
    updatedAt = this.updatedAt.toKotlinInstant(),
)

/**
 * Converts a [Branch] domain model to a [NetworkBranch] data transfer object.
 */
fun Branch.asNetworkModel(): NetworkBranch = NetworkBranch(
    id = this.id,
    name = this.name,
    phone = this.phone,
    status = this.status,
    createdAt = this.createdAt.toFirestoreTimestamp(),
    updatedAt = this.updatedAt.toFirestoreTimestamp(),
)

/**
 * Converts a [BranchEntity] Room entity to a [NetworkBranch] data transfer object.
 */

fun BranchEntity.asNetworkModel(): NetworkBranch = NetworkBranch(
    id = this.branchId,
    name = this.name,
    phone = this.phone,
    status = BranchStatus.fromValue(this.status),
    createdAt = this.createdAt.toFirestoreTimestamp(),
    updatedAt = this.updatedAt.toFirestoreTimestamp(),
)

/**
 * Converts a [NetworkBranch] data transfer object to a [BranchEntity] Room entity.
 */
fun NetworkBranch.asEntity(businessId: String): BranchEntity = BranchEntity(
    branchId = this.id,
    businessId = businessId,
    name = this.name,
    phone = this.phone,
    status = this.status.value,
    createdAt = this.createdAt.toKotlinInstant(),
    updatedAt = this.updatedAt.toKotlinInstant(),
)
