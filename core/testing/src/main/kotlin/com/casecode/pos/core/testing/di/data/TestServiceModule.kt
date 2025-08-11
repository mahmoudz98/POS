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
package com.casecode.pos.core.testing.di.data

import com.casecode.pos.core.data.di.ServiceModule
import com.casecode.pos.core.domain.service.GoogleAuthUiClient
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.domain.service.SubscriptionService
import com.casecode.pos.core.testing.services.TestGoogleAuthUiClient
import com.casecode.pos.core.testing.services.TestLogService
import com.casecode.pos.core.testing.services.TestSubscriptionService
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ServiceModule::class],
)
interface TestServiceModule {

    @Binds
    @Singleton
    abstract fun bindTestLogService(logServiceImpl: TestLogService): LogService

    @Binds
    abstract fun bindTestSubscriptionService(impl: TestSubscriptionService): SubscriptionService

    @Binds
    abstract fun bindTestGoogleAuthUiClient(impl: TestGoogleAuthUiClient): GoogleAuthUiClient
}
