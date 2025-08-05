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
package com.casecode.pos.core.firebase.datasource

import com.casecode.pos.core.firebase.model.NetworkSubscriptionPlan
import com.casecode.pos.core.firebase.model.SupportedCurrency
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Named

class RemoteConfigDataSourceImpl @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    private val json: Json,
    @Named("SubscriptionPlansJsonKey") private val plansJsonKey: String,
    @Named("CurrenciesJsonKey") private val currenciesJsonKey: String,
) : ConfigDataSource {

    override suspend fun getSubscriptionPlans(): List<NetworkSubscriptionPlan> {
        remoteConfig.fetchAndActivate().await()
        val jsonString = remoteConfig.getString(plansJsonKey)
        return json.decodeFromString<List<NetworkSubscriptionPlan>>(jsonString)
    }

    override suspend fun getSupportedCurrencies(): List<SupportedCurrency> {
        remoteConfig.fetchAndActivate().await()
        val jsonString = remoteConfig.getString(currenciesJsonKey)
        return json.decodeFromString<List<SupportedCurrency>>(jsonString)
    }
}