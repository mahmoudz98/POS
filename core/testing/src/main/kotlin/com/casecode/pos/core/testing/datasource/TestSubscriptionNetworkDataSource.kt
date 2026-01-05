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
package com.casecode.pos.core.testing.datasource

import com.casecode.pos.core.firebase.datasource.SubscriptionNetworkDataSource
import com.casecode.pos.core.firebase.model.NetworkBillingEvent
import com.casecode.pos.core.firebase.model.NetworkSubscription
import javax.inject.Inject

class TestSubscriptionNetworkDataSource @Inject
constructor() : SubscriptionNetworkDataSource {
    private var subscription: NetworkSubscription? = null
    private var billingHistory = mutableListOf<NetworkBillingEvent>()
    var updateCreditBalanceCallCount = 0

    override suspend fun getSubscription(businessId: String): NetworkSubscription? = subscription

    override suspend fun getBillingHistory(businessId: String): List<NetworkBillingEvent> = billingHistory

    override suspend fun updateCreditBalanceAndLogEvent(
        businessId: String,
        newBalance: Long,
        event: NetworkBillingEvent,
    ) {
        updateCreditBalanceCallCount++
        subscription = subscription?.copy(creditBalance = newBalance.toInt())
        billingHistory.add(event)
    }

    fun setSubscription(subscription: NetworkSubscription?) {
        this.subscription = subscription
    }

    fun setBillingHistory(history: List<NetworkBillingEvent>) {
        this.billingHistory = history.toMutableList()
    }
}
