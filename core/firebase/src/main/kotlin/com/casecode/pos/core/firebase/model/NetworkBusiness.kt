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
package com.casecode.pos.core.firebase.model

import com.casecode.pos.core.model.business.BusinessStatus
import com.casecode.pos.core.model.business.Vertical
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class NetworkBusiness(
    @DocumentId val id: String = "",
    val name: String = "",
    val ownerUid: String = "",
    val vertical: Int = Vertical.RETAIL.value,
    val companyCode: String = "",
    val currencyCode: String = "",
    val status: Int = BusinessStatus.ACTIVE.value,
    val email: String = "",
    val phone: String = "",
    @ServerTimestamp val updatedAt: Timestamp = Timestamp.now(),
    @ServerTimestamp val createdAt: Timestamp = Timestamp.now(),
)