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

import com.casecode.pos.core.data.utils.toKotlinInstant
import com.casecode.pos.core.firebase.model.NetworkBusiness
import com.casecode.pos.core.model.data.business.Business
import com.casecode.pos.core.model.data.business.BusinessStatus
import com.casecode.pos.core.model.data.business.Vertical


fun NetworkBusiness.asExternalModel(): Business = Business(
    id = this.id,
    name = this.name,
    ownerUid = this.ownerUid,
    vertical = Vertical.entries.firstOrNull { it.value == this.vertical }
        ?: Vertical.RETAIL,
    companyCode = this.companyCode ,
    currencyCode = this.currencyCode,
    status = BusinessStatus.entries.firstOrNull { it.value == this.status }
        ?: BusinessStatus.INACTIVE,
    email = this.email,
    phone = this.phone,
    updatedAt = this.updatedAt.toKotlinInstant(),
    createdAt = this.createdAt.toKotlinInstant()
)

fun Business.asNetworkModel(): NetworkBusiness = NetworkBusiness(
    id = this.id,
    name = this.name,
    ownerUid = this.ownerUid,
    vertical = this.vertical.value,
    companyCode = this.companyCode,
    currencyCode = this.currencyCode,
    status = this.status.value,
    email = this.email,
    phone = this.phone,
)


