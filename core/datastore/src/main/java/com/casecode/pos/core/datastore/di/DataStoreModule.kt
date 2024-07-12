package com.casecode.pos.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.casecode.pos.core.datastore.LoginPreferencesSerializer
import com.casecode.pos.core.common.AppDispatchers
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.common.di.ApplicationScope
import com.casecode.pos.core.datastore.LoginPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    internal fun providesLoginPreferencesDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        loginPreferencesSerializer: LoginPreferencesSerializer,
    ): DataStore<LoginPreferences> =
        DataStoreFactory.create(
            serializer = loginPreferencesSerializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher),

        ) {
            context.dataStoreFile("login_preferences.pb")
        }
}