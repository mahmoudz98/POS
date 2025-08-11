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
package com.casecode.pos.core.database.di

import com.casecode.pos.core.database.PosDatabase
import com.casecode.pos.core.database.dao.BranchDao
import com.casecode.pos.core.database.dao.BusinessDao
import com.casecode.pos.core.database.dao.LocalSignalDao
import com.casecode.pos.core.database.dao.OutboxCommandDao
import com.casecode.pos.core.database.dao.SubscriptionDao
import com.casecode.pos.core.database.dao.TaxRateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun provideBusinessDao(database: PosDatabase): BusinessDao = database.businessDao()

    @Provides
    fun provideBranchDao(database: PosDatabase): BranchDao = database.branchDao()

    @Provides
    fun provideOutboxCommandDao(database: PosDatabase): OutboxCommandDao = database.outboxCommandDao()

    @Provides
    fun provideLocalSignalDao(database: PosDatabase): LocalSignalDao = database.localSignalDao()

    @Provides
    fun provideSubscriptionDao(database: PosDatabase): SubscriptionDao = database.subscriptionDao()

    @Provides
    fun provideTaxRateDao(database: PosDatabase): TaxRateDao = database.taxRateDao()
}