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
import com.casecode.pos.core.model.data.business.Business
import com.casecode.pos.core.model.data.business.BusinessStatus
import com.casecode.pos.core.model.data.business.Vertical
import kotlinx.datetime.Instant

@Entity(tableName = "business")
data class BusinessEntity(
    @PrimaryKey
    val uid: String,
    val name: String,
    @ColumnInfo(name = "owner_uid") val ownerUid: String,
    val vertical: Int,
    @ColumnInfo(name = "company_code") val companyCode: String,
    @ColumnInfo(name = "currency_code") val currencyCode: String,
    val status: Int,
    val email: String,
    val phone: String,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false,
)

fun BusinessEntity.asExternalModel(): Business {
    return Business(
        id = this.uid,
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
        uid = this.id,
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