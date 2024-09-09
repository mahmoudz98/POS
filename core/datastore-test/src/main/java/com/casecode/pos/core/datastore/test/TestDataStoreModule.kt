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
package com.casecode.pos.core.datastore.test

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.casecode.pos.core.common.di.ApplicationScope
import com.casecode.pos.core.datastore.LoginPreferences
import com.casecode.pos.core.datastore.LoginPreferencesSerializer
import com.casecode.pos.core.datastore.di.DataStoreModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineScope
import org.junit.rules.TemporaryFolder
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreModule::class],
)
internal object TestDataStoreModule {
    @Provides
    @Singleton
    fun providesLoginPreferencesDataStore(
        @ApplicationScope scope: CoroutineScope,
        loginPreferencesSerializer: LoginPreferencesSerializer,
        tmpFolder: TemporaryFolder,
    ): DataStore<LoginPreferences> =
        tmpFolder.testLoginPreferencesDataStore(
            coroutineScope = scope,
            loginPreferencesSerializer = loginPreferencesSerializer,
        )
}

fun TemporaryFolder.testLoginPreferencesDataStore(
    coroutineScope: CoroutineScope,
    loginPreferencesSerializer: LoginPreferencesSerializer = LoginPreferencesSerializer(),
) = DataStoreFactory.create(
    serializer = loginPreferencesSerializer,
    scope = coroutineScope,
) {
    try {
        newFile("login_preferences_test.pb")
    } catch (e: Exception) {
        println("${e.message}")
        throw e
    }
}