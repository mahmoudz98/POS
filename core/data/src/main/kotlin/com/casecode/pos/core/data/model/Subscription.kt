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
import com.casecode.pos.core.database.model.SubscriptionEntity
import com.casecode.pos.core.firebase.model.NetworkSubscription
import com.casecode.pos.core.model.business.Subscription
import com.casecode.pos.core.model.business.SubscriptionStatus

/**
 * Converts a [NetworkSubscription] data transfer object to a [Subscription] domain model.
 */
fun NetworkSubscription.asExternalModel(): Subscription = Subscription(
    planId = this.planId,
    planName = this.planId,
    status = this.status ?: SubscriptionStatus.CANCELED,
    creditBalance = this.creditBalance,
    currentPeriodEndDate = this.currentPeriodEndDate.toKotlinInstant(),
    updatedAt = this.updatedAt.toKotlinInstant(),
)

/**
 * Converts a [Subscription] domain model to a [NetworkSubscription] data transfer object.
 */
fun Subscription.asNetworkModel(): NetworkSubscription = NetworkSubscription(
    planId = this.planId,
    planName = this.planName,
    status = this.status,
    creditBalance = this.creditBalance,
    currentPeriodEndDate = this.currentPeriodEndDate.toFirestoreTimestamp(),
    updatedAt = this.updatedAt.toFirestoreTimestamp(),
)

/**
 * Converts a [SubscriptionEntity] Room entity to a [NetworkSubscription] data transfer object.
 */
fun SubscriptionEntity.asNetworkModel(): NetworkSubscription = NetworkSubscription(
    planId = this.planId,
    planName = this.planName,
    status = SubscriptionStatus.fromValue(this.status),
    creditBalance = this.creditBalance,
    currentPeriodEndDate = this.currentPeriodEndDate.toFirestoreTimestamp(),
    updatedAt = this.updatedAt.toFirestoreTimestamp(),
)
