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

import com.casecode.pos.core.firebase.model.NetworkBranch
import com.casecode.pos.core.model.data.business.Branch
import com.casecode.pos.core.model.data.business.BranchStatus
import kotlinx.datetime.Instant


fun NetworkBranch.asExternalModel(): Branch = Branch(
    id = this.id,
    name = this.name ?: "",
    phone = this.phone,
    status = this.status ?: BranchStatus.CLOSED,
    createdAt = this.createdAt?.toInstant()?.let { Instant.fromEpochMilliseconds(it.epochSecond) },
    updatedAt = this.updatedAt?.toInstant()?.let { Instant.fromEpochMilliseconds(it.epochSecond) },
)

fun Branch.asNetworkModel(): NetworkBranch = NetworkBranch(
    id = this.id,
    name = this.name,
    phone = this.phone,
    status = this.status,
)
