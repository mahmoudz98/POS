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

import com.casecode.pos.core.data.repository.business.BranchRepositoryImpl
import com.casecode.pos.core.data.repository.business.BusinessRepositoryImpl
import com.casecode.pos.core.data.repository.business.EmployeeRepositoryImpl
import com.casecode.pos.core.domain.utils.Syncable
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {
    @Binds
    @IntoSet
    @Singleton
    abstract fun bindSyncableBusinessRepository(impl: BusinessRepositoryImpl): Syncable

    @Binds
    @IntoSet
    @Singleton
    abstract fun bindSyncableEmployeeRepository(impl: EmployeeRepositoryImpl): Syncable

    @Binds
    @IntoSet
    @Singleton
    abstract fun bindSyncableBranchRepository(impl: BranchRepositoryImpl): Syncable
}
