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

import com.casecode.pos.core.firebase.model.NetworkBillingEvent
import com.casecode.pos.core.model.data.business.BillingEvent
import com.casecode.pos.core.model.data.business.BillingEventType
import kotlinx.datetime.Instant
import java.math.BigDecimal


fun NetworkBillingEvent.asExternalModel(): BillingEvent = BillingEvent(
    id = this.id,
    eventType = this.eventType ?: BillingEventType.CREDIT_ADJUSTMENT,
    description = this.description ?: "",
    amount = BigDecimal.valueOf(this.amount ?: 0.0),
    currencyCode = this.currencyCode ?: "",
    creditsChange = this.creditsChange ?: 0L,
    eventDate = this.eventDate?.toInstant()?.let { Instant.fromEpochMilliseconds(it.epochSecond) },
    paymentProviderTransactionId = this.paymentProviderTransactionId,
)

fun BillingEvent.asNetworkModel(): NetworkBillingEvent = NetworkBillingEvent(
    id = this.id,
    eventType = this.eventType,
    description = this.description,
    amount = this.amount.toDouble(),
    currencyCode = this.currencyCode,
    creditsChange = this.creditsChange,
    paymentProviderTransactionId = this.paymentProviderTransactionId,
)
