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
package com.casecode.pos.core.data.di

import com.casecode.pos.core.data.service.GoogleAuthUiClientImpl
import com.casecode.pos.core.data.service.LogServiceImpl
import com.casecode.pos.core.data.service.RevenueCatSubscriptionServiceImpl
import com.casecode.pos.core.domain.service.GoogleAuthUiClient
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.domain.service.SubscriptionService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    internal abstract fun bindLogService(logServiceImpl: LogServiceImpl): LogService

    @Binds
    abstract fun bindSubscriptionService(impl: RevenueCatSubscriptionServiceImpl): SubscriptionService

    @Binds
    abstract fun bindGoogleAuthUiClient(impl: GoogleAuthUiClientImpl): GoogleAuthUiClient
}
