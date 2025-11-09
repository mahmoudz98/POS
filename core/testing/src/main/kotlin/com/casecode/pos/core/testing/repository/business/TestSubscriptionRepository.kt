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
package com.casecode.pos.core.testing.repository.business

import com.casecode.pos.core.domain.repository.business.SubscriptionRepository
import com.casecode.pos.core.model.business.BillingEvent
import com.casecode.pos.core.model.business.OnboardingConfig
import com.casecode.pos.core.model.business.Subscription
import com.casecode.pos.core.testing.base.TestRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestSubscriptionRepository @Inject constructor() : TestRepository(), SubscriptionRepository {

    private var fakeOnboardingConfig = OnboardingConfig(emptyList(), emptyList())
    private val subscriptions = mutableMapOf<String, Subscription>()
    private val billingHistories = mutableMapOf<String, MutableList<BillingEvent>>()

    override suspend fun getOnboardingConfig(): Result<OnboardingConfig> {
        getFailureResult<OnboardingConfig>()?.let { return it }
        return Result.success(fakeOnboardingConfig)
    }

    override suspend fun getCurrentSubscription(businessId: String): Result<Subscription?> {
        getFailureResult<Subscription?>()?.let { return it }
        return Result.success(subscriptions[businessId])
    }

    override suspend fun getBillingHistory(businessId: String): Result<List<BillingEvent>> {
        getFailureResult<List<BillingEvent>>()?.let { return it }
        return Result.success(billingHistories[businessId] ?: emptyList())
    }

    override suspend fun purchaseCredits(businessId: String, event: BillingEvent, creditsToAdd: Long): Result<Unit> {
        getFailureResult<Unit>()?.let { return it }

        val currentSub = subscriptions[businessId]
        if (currentSub != null) {
            val updatedSub = currentSub.copy(creditBalance = (currentSub.creditBalance + creditsToAdd).toInt())
            subscriptions[businessId] = updatedSub
        }

        val history = billingHistories.getOrPut(businessId) { mutableListOf() }
        history.add(0, event)
        return Result.success(Unit)
    }

    fun setOnboardingConfig(config: OnboardingConfig) {
        this.fakeOnboardingConfig = config
    }

    fun setCurrentSubscription(businessId: String, subscription: Subscription) {
        subscriptions[businessId] = subscription
    }

    override fun clear() {
        returnSuccess()
        fakeOnboardingConfig = OnboardingConfig(emptyList(), emptyList())
        subscriptions.clear()
        billingHistories.clear()
    }
}
