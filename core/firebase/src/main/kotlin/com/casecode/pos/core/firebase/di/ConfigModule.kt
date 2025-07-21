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
package com.casecode.pos.core.firebase.di

import com.casecode.pos.core.firebase.BuildConfig
import com.casecode.pos.core.firebase.R
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance().apply {
        setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 30 else 3_600
            },
        )
        setDefaultsAsync(R.xml.core_firebase_services_remote_config_defaults)
    }

    /**
     * Provides a Gson instance for JSON serialization and deserialization.
     *
     * @return A Gson instance.
     */
    @Provides
    @Singleton
    fun providesNetworkJson(): Json =
        Json {
            isLenient = true
            ignoreUnknownKeys = true
        }

    @Provides
    @Named("SubscriptionPlansJsonKey")
    fun provideSubscriptionPlansKey(): String = "subscription_plans"

    @Provides
    @Named("CurrenciesJsonKey")
    fun provideCurrenciesJsonKey(): String = "supported_currencies_json"
}