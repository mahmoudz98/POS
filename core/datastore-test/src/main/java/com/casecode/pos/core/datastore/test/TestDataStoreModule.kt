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
    }catch (e: Exception){
        println("${e.message}")
        throw e
    }
}