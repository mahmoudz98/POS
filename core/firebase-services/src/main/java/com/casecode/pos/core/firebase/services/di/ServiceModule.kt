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
package com.casecode.pos.core.firebase.services.di

import com.casecode.pos.core.firebase.services.AuthService
import com.casecode.pos.core.firebase.services.AuthServiceImpl
import com.casecode.pos.core.firebase.services.LogService
import com.casecode.pos.core.firebase.services.LogServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    internal abstract fun bindAuthService(authServiceImpl: AuthServiceImpl): AuthService

    @Binds
    internal abstract fun bindLogService(logServiceImpl: LogServiceImpl): LogService

}