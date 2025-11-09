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
package com.casecode.pos.core.domain.usecase

import android.app.Activity
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.domain.service.SubscriptionService
import com.casecode.pos.core.model.business.SubscriptionPlan
import com.casecode.pos.core.model.data.PurchaseResult
import kotlinx.coroutines.delay
import javax.inject.Inject

class ProcessPlanPurchaseUseCase
@Inject
constructor(
    private val subscriptionService: SubscriptionService,
    private val logService: LogService,
) {
    suspend operator fun invoke(activity: Activity, planToPurchase: SubscriptionPlan): Result<PurchaseResult> {
        logService.log("ProcessPlanPurchaseUseCase: Initiating for plan: ${planToPurchase.id}")

        if (planToPurchase.isFree) {
            logService.log("ProcessPlanPurchaseUseCase: Plan is free, returning mock success without calling payment gateway.")
            return Result.success(
                PurchaseResult(
                    providerTransactionId = "free_plan_activation_${System.currentTimeMillis()}",
                    wasSuccessful = true,
                ),
            )
        }
        delay(200)
        return subscriptionService.purchasePlan(activity, planToPurchase)
    }
}
