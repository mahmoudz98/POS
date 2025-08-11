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
import com.casecode.pos.core.database.model.TaxRateEntity
import com.casecode.pos.core.firebase.model.NetworkTaxRate
import com.casecode.pos.core.model.business.TaxRate

/**
 * Converts a [NetworkTaxRate] data transfer object to a [TaxRate] domain model.
 */
fun NetworkTaxRate.asExternalModel(): TaxRate = TaxRate(
    id = this.id,
    name = this.name,
    rate = this.rate,
    isIncludedInPrice = this.isIncludedInPrice,
    isDefault = this.isDefault,
    createdAt = this.createdAt.toKotlinInstant(),
    updatedAt = this.updatedAt.toKotlinInstant(),
)

/**
 * Converts a [TaxRate] domain model to a [NetworkTaxRate] data transfer object.
 */
fun TaxRate.asNetworkModel(): NetworkTaxRate = NetworkTaxRate(
    id = this.id,
    name = this.name,
    rate = this.rate,
    isIncludedInPrice = this.isIncludedInPrice,
    isDefault = this.isDefault,
    createdAt = this.createdAt.toFirestoreTimestamp(),
    updatedAt = this.updatedAt.toFirestoreTimestamp(),
)
fun TaxRateEntity.asNetworkModel(): NetworkTaxRate = NetworkTaxRate(
    id = this.id,
    name = this.name,
    rate = this.rate,
    isIncludedInPrice = this.isIncludedInPrice,
    isDefault = this.isDefault,
    createdAt = this.createdAt.toFirestoreTimestamp(),
    updatedAt = this.updatedAt.toFirestoreTimestamp(),
)
