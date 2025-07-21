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
package com.casecode.pos.core.model.business

import kotlinx.datetime.Instant

data class BillingEvent(
    val id: String,
    val eventType: BillingEventType,
    val description: String,
    val amount: Double,
    val currencyCode: String,
    val creditsChange: Int,
    val eventDate: Instant?,
    val paymentProviderTransactionId: String?,
) {
    companion object {
        /**
         * Factory function to create the first BillingEvent when a plan is activated.
         */
        fun forPlanActivation(
            plan: SubscriptionPlan,
            currencyCode: String,
            paymentId: String? = null,
        ): BillingEvent {
            return BillingEvent(
                id = "", // Will be set by Firestore
                eventType = BillingEventType.CHARGE_SUCCESSFUL,
                description = "Activated plan: ${plan.featuresEn}",
                amount = plan.prices?.first()?.amount?:0.0,
                currencyCode = currencyCode,
                creditsChange = plan.limits.initialCredits,
                eventDate = null, // Firestore will set this
                paymentProviderTransactionId = paymentId,
            )
        }
    }
}
