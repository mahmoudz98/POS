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
package com.casecode.pos.core.data.repository.business

import com.casecode.pos.core.common.AppDispatchers
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.data.model.asExternalModel
import com.casecode.pos.core.data.model.asNetworkModel
import com.casecode.pos.core.domain.repository.business.SubscriptionRepository
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.firebase.datasource.ConfigDataSource
import com.casecode.pos.core.firebase.datasource.SubscriptionNetworkDataSource
import com.casecode.pos.core.model.data.business.BillingEvent
import com.casecode.pos.core.model.data.business.OnboardingConfig
import com.casecode.pos.core.model.data.business.Subscription
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "SubscriptionRepoImpl"

class SubscriptionRepositoryImpl @Inject constructor(
    private val networkDataSource: SubscriptionNetworkDataSource,
    private val configDataSource: ConfigDataSource,
    private val logService: LogService,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : SubscriptionRepository {

    /** In-memory cache for onboarding configuration. */
    private var cachedOnboardingConfig: OnboardingConfig? = null

    override suspend fun getOnboardingConfig(): Result<OnboardingConfig> {
        cachedOnboardingConfig?.let { return Result.success(it) }

        return runCatching {
            val plans = configDataSource.getSubscriptionPlans()
            val currencies = configDataSource.getSupportedCurrencies()

            OnboardingConfig(plans, currencies).also { config ->
                cachedOnboardingConfig = config
                logService.log("SubscriptionRepo: Fetched and cached new onboarding config.")
            }
        }.onFailure { e ->
            logService.log("SubscriptionRepo: Failed to get full onboarding config. ${e.message}")
        }
    }


    override suspend fun getCurrentSubscription(businessId: String): Result<Subscription?> =
        withContext(ioDispatcher) {
            runCatching {
                networkDataSource.getSubscription(businessId)?.asExternalModel()
            }.onFailure { e ->
                logService.log("$TAG: getCurrentSubscription failed for businessId: $businessId, ${e.message}")
            }
        }

    override suspend fun getBillingHistory(businessId: String): Result<List<BillingEvent>> =
        withContext(ioDispatcher) {
            runCatching {
                networkDataSource.getBillingHistory(businessId).map { it.asExternalModel() }
            }.onFailure { e ->
                logService.log("$TAG: getBillingHistory failed for businessId: $businessId , ${e.message}")
            }
        }

    override suspend fun purchaseCredits(
        businessId: String,
        event: BillingEvent,
        creditsToAdd: Long,
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val currentSub = getCurrentSubscription(businessId).getOrThrow()
                ?: throw IllegalStateException("Business has no active subscription to add credits to.")

            val newBalance = currentSub.creditBalance + creditsToAdd
            networkDataSource.updateCreditBalanceAndLogEvent(
                businessId,
                newBalance,
                event.asNetworkModel(),
            )
        }.onFailure { e ->
            logService.log("SubscriptionRepo: purchaseCredits failed for business $businessId")
        }

    }
}
