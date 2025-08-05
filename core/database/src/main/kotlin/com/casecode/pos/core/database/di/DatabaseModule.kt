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

import android.content.Context
import androidx.room.Room
import com.casecode.pos.core.database.PosDatabase
import com.casecode.pos.core.database.PosDatabaseTransactionRunner
import com.casecode.pos.core.database.util.DatabaseTransactionRunner
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DatabaseModule {
    @Binds
    fun bindsDatabaseTransactionRunner(impl: PosDatabaseTransactionRunner): DatabaseTransactionRunner

    companion object {
        @Provides
        @Singleton
        fun providesNiaDatabase(
            @ApplicationContext context: Context,
        ): PosDatabase = Room.databaseBuilder(
            context,
            PosDatabase::class.java,
            "pos-database",
        ).build()
    }
}