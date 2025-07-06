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
package com.casecode.pos.core.model.data.business

import kotlinx.datetime.Instant

data class Subscription(
    val planId: String,
    val planName: String,
    val status: SubscriptionStatus,
    val creditBalance: Long,
    val currentPeriodEndDate: Instant?,
    val updatedAt: Instant?,
) {
    companion object {
        /**
         * Factory function to create a new, initial Subscription state
         * directly from a selected SubscriptionPlan.
         */
        fun fromPlan(plan: SubscriptionPlan, trialEndDate: Instant? = null): Subscription {
            return Subscription(
                planId = plan.id,
                planName = plan.name,
                status = if (trialEndDate != null) SubscriptionStatus.TRIAL else SubscriptionStatus.ACTIVE,
                creditBalance = plan.limits.initialCredits,
                currentPeriodEndDate = trialEndDate,
                updatedAt = null, // Firestore will set this
            )
        }
    }
}