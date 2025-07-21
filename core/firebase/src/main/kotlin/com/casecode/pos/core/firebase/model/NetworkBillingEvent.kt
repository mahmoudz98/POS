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

import com.casecode.pos.core.model.business.BillingEventType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Data class representing a billing event for network transfer.
 * This class is used for serialization and deserialization of billing event data
 * to/from the remote data source (e.g., Firestore).
 *
 * @property id The document ID of the billing event.
 * @property eventType The type of billing event.
 * @property description A description of the event.
 * @property amount The monetary amount associated with the event.
 * @property currencyCode The currency code for the amount.
 * @property creditsChange The change in transaction credits.
 * @property eventDate The date and time the event occurred (server timestamp).
 * @property paymentProviderTransactionId The transaction ID from the payment provider.
 */
data class NetworkBillingEvent(
    @DocumentId val id: String = "",
    val eventType: BillingEventType? = null,
    val description: String? = null,
    val amount: Double? = null,
    val currencyCode: String? = null,
    val creditsChange: Int? = 0,
    @ServerTimestamp val eventDate: Timestamp? = null,
    val paymentProviderTransactionId: String? = null,
)
