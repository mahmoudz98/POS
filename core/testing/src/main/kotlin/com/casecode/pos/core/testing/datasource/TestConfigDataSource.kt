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

import com.casecode.pos.core.firebase.datasource.ConfigDataSource
import com.casecode.pos.core.firebase.model.NetworkSubscriptionPlan
import com.casecode.pos.core.firebase.model.SupportedCurrency
import javax.inject.Inject

class TestConfigDataSource @Inject
constructor() : ConfigDataSource {

    private var shouldReturnError = false
    private val subscriptionPlans = mutableListOf<NetworkSubscriptionPlan>()
    private val supportedCurrencies = mutableListOf<SupportedCurrency>()

    override suspend fun getSubscriptionPlans(): List<NetworkSubscriptionPlan> {
        if (shouldReturnError) {
            throw Exception("Network error")
        }
        return subscriptionPlans
    }

    override suspend fun getSupportedCurrencies(): List<SupportedCurrency> {
        if (shouldReturnError) {
            throw Exception("Network error")
        }
        return supportedCurrencies
    }

    fun setShouldReturnError(shouldReturnError: Boolean) {
        this.shouldReturnError = shouldReturnError
    }

    fun setSubscriptionPlans(plans: List<NetworkSubscriptionPlan>) {
        subscriptionPlans.clear()
        subscriptionPlans.addAll(plans)
    }

    fun setSupportedCurrencies(currencies: List<SupportedCurrency>) {
        supportedCurrencies.clear()
        supportedCurrencies.addAll(currencies)
    }
}
