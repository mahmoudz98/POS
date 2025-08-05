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
import com.casecode.pos.core.firebase.model.NetworkBillingEvent
import com.casecode.pos.core.model.business.BillingEvent

fun NetworkBillingEvent.asExternalModel(): BillingEvent = BillingEvent(
    id = this.id,
    eventType = this.eventType,
    description = this.description,
    amount = this.amount,
    currencyCode = this.currencyCode,
    creditsChange = this.creditsChange,
    eventDate = this.eventDate.toKotlinInstant(),
    paymentProviderTransactionId = this.paymentProviderTransactionId,
)

/**
 * Converts a [BillingEvent] domain model to a [NetworkBillingEvent] data transfer object.
 */
fun BillingEvent.asNetworkModel(): NetworkBillingEvent = NetworkBillingEvent(
    id = this.id,
    eventType = this.eventType,
    description = this.description,
    amount = this.amount,
    currencyCode = this.currencyCode,
    creditsChange = this.creditsChange,
    eventDate = this.eventDate.toFirestoreTimestamp(),
    paymentProviderTransactionId = this.paymentProviderTransactionId,
)
