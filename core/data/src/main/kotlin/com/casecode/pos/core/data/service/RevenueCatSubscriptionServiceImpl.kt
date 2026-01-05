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
package com.casecode.pos.core.data.service

import android.app.Activity
import com.casecode.pos.core.domain.exceptions.PaymentCancelledException
import com.casecode.pos.core.domain.exceptions.PaymentProviderException
import com.casecode.pos.core.domain.exceptions.PlanNotFoundInProviderException
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.domain.service.SubscriptionService
import com.casecode.pos.core.model.business.SubscriptionPlan
import com.casecode.pos.core.model.data.PurchaseResult
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.purchaseWith
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class RevenueCatSubscriptionServiceImpl @Inject constructor(
    private val logService: LogService,
) : SubscriptionService {

    override suspend fun purchasePlan(activity: Activity, plan: SubscriptionPlan): Result<PurchaseResult> {
        return suspendCancellableCoroutine { continuation ->
            logService.log("RevenueCat: Attempting to fetch offerings to find plan: ${plan.id}")

            Purchases.sharedInstance.getOfferingsWith(
                onError = { error ->
                    val ex = PaymentProviderException("RevenueCat: Failed to get offerings - ${error.message}")
                    logService.logNonFatalCrash(ex)
                    if (continuation.isActive) continuation.resume(Result.failure(ex))
                },
                onSuccess = { offerings ->
                    val packageToPurchase: Package? = offerings.current?.availablePackages?.find { pkg ->
                        pkg.product.id == plan.id
                    }
                    if (packageToPurchase == null) {
                        val ex = PlanNotFoundInProviderException(plan.id)
                        logService.logNonFatalCrash(ex)
                        if (continuation.isActive) continuation.resume(Result.failure(ex))
                        return@getOfferingsWith
                    }

                    logService.log("RevenueCat: Found package. Launching purchase flow for SKU: ${packageToPurchase.product.id}")
                    val purchaseParams = PurchaseParams.Builder(activity, packageToPurchase).build()
                    Purchases.sharedInstance.purchaseWith(
                        purchaseParams,
                        onError = { error, userCancelled ->
                            val ex = if (userCancelled) {
                                PaymentCancelledException()
                            } else {
                                PaymentProviderException("RevenueCat: Purchase failed - ${error.message}")
                            }
                            logService.logNonFatalCrash(ex)
                            if (continuation.isActive) continuation.resume(Result.failure(ex))
                        },
                        onSuccess = { storeTransaction: StoreTransaction?, customerInfo ->
                            val uniqueTransactionId = storeTransaction?.orderId ?: storeTransaction?.purchaseToken

                            if (uniqueTransactionId == null) {
                                val ex = PaymentProviderException("Purchase succeeded but failed to retrieve a transaction identifier.")
                                logService.logNonFatalCrash(ex)
                                if (continuation.isActive) continuation.resume(Result.failure(ex))
                                return@purchaseWith
                            }

                            logService.log("RevenueCat: Purchase successful for plan ${plan.id}. Provider Transaction ID: $uniqueTransactionId")

                            val purchaseResult = PurchaseResult(
                                providerTransactionId = uniqueTransactionId,
                                wasSuccessful = true,
                            )
                            if (continuation.isActive) continuation.resume(Result.success(purchaseResult))
                        },
                    )
                },
            )
        }
    }
}
