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
import com.casecode.pos.core.model.business.TaxRate
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(tableName = "tax_rates")
data class TaxRateEntity(
    @PrimaryKey
    val id: String = Uuid.random().toString(),
    val name: String,
    val rate: Float,
    @ColumnInfo(name = "is_included_in_price") val isIncludedInPrice: Boolean,
    @ColumnInfo(name = "is_default") val isDefault: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant,
)

fun TaxRateEntity.asExternalModel(): TaxRate {
    return TaxRate(
        id = this.id,
        name = this.name,
        rate = this.rate,
        isIncludedInPrice = this.isIncludedInPrice,
        isDefault = this.isDefault,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}

fun TaxRate.asEntity(): TaxRateEntity {
    return TaxRateEntity(
        id = this.id,
        name = this.name,
        rate = this.rate,
        isIncludedInPrice = this.isIncludedInPrice,
        isDefault = this.isDefault,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}
