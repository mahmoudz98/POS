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

import com.casecode.pos.core.firebase.datasource.AuthRemoteDataSource
import com.casecode.pos.core.firebase.datasource.BusinessNetworkDataSource
import com.casecode.pos.core.firebase.datasource.ConfigDataSource
import com.casecode.pos.core.firebase.datasource.EmployeeNetworkDataSource
import com.casecode.pos.core.firebase.datasource.FirebaseAuthDataSourceImpl
import com.casecode.pos.core.firebase.datasource.FirebaseBusinessDataSourceImpl
import com.casecode.pos.core.firebase.datasource.FirebaseEmployeeDataSourceImpl
import com.casecode.pos.core.firebase.datasource.FirebaseInboxNetworkDataSource
import com.casecode.pos.core.firebase.datasource.FirebaseSubscriptionDataSourceImpl
import com.casecode.pos.core.firebase.datasource.FirebaseTaxDataSourceImpl
import com.casecode.pos.core.firebase.datasource.InboxNetworkDataSource
import com.casecode.pos.core.firebase.datasource.RemoteConfigDataSourceImpl
import com.casecode.pos.core.firebase.datasource.SubscriptionNetworkDataSource
import com.casecode.pos.core.firebase.datasource.TaxNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseDataSourceModule {
    @Binds
    abstract fun bindBusinessNetworkDataSource(impl: FirebaseBusinessDataSourceImpl): BusinessNetworkDataSource

    @Binds
    abstract fun bindInboxNetworkDataSource(impl: FirebaseInboxNetworkDataSource): InboxNetworkDataSource

    @Binds
    abstract fun bindAuthRemoteDataSource(impl: FirebaseAuthDataSourceImpl): AuthRemoteDataSource

    @Binds
    abstract fun bindTaxNetworkDataSource(impl: FirebaseTaxDataSourceImpl): TaxNetworkDataSource

    @Binds
    abstract fun bindSubscriptionNetworkDataSource(impl: FirebaseSubscriptionDataSourceImpl): SubscriptionNetworkDataSource

    @Binds
    abstract fun bindConfigDataSource(impl: RemoteConfigDataSourceImpl): ConfigDataSource

    @Binds
    abstract fun bindEmployeeNetworkDataSource(impl: FirebaseEmployeeDataSourceImpl): EmployeeNetworkDataSource
}
