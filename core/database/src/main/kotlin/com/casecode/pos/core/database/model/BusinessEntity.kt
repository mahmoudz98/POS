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
import com.casecode.pos.core.model.business.Business
import com.casecode.pos.core.model.business.BusinessStatus
import com.casecode.pos.core.model.business.Vertical
import kotlin.time.Instant

/**
 * Defines a business entity for the local Room database.
 *
 * @property businessId The unique ID of the business.
 * @property name The name of the business.
 * @property ownerUid The Firebase UID of the business owner.
 * @property vertical The industry vertical of the business (e.g., Retail, Cafe).
 * @property companyCode The unique company code for employee logins.
 * @property currencyCode The default currency code for the business (e.g., "USD").
 * @property status The current status of the business (e.g., ACTIVE, INACTIVE).
 * @property email The contact email of the business.
 * @property phone The contact phone number of the business.
 * @property updatedAt The timestamp of the last update to this business record.
 * @property createdAt The timestamp of when this business record was created.
 */
@Entity(tableName = "business")
data class BusinessEntity(
    @PrimaryKey
    val businessId: String,
    val name: String,
    @ColumnInfo("owner_uid")val ownerUid: String,
    val vertical: Int,
    val companyCode: String,
    val currencyCode: String,
    val status: Int,
    val email: String,
    val phone: String,
    val updatedAt: Instant,
    val createdAt: Instant,
)

fun BusinessEntity.asExternalModel(): Business {
    return Business(
        id = this.businessId,
        name = this.name,
        ownerUid = this.ownerUid,
        vertical = Vertical.fromValue(this.vertical),
        companyCode = this.companyCode,
        currencyCode = this.currencyCode,
        status = BusinessStatus.fromValue(this.status),
        email = this.email,
        phone = this.phone,
        updatedAt = this.updatedAt,
        createdAt = this.createdAt,
    )
}

fun Business.asEntity(): BusinessEntity {
    return BusinessEntity(
        businessId = this.id,
        name = this.name,
        ownerUid = this.ownerUid,
        vertical = this.vertical.value,
        companyCode = this.companyCode,
        currencyCode = this.currencyCode,
        status = this.status.value,
        email = this.email,
        phone = this.phone,
        updatedAt = this.updatedAt,
        createdAt = this.createdAt,
    )
}