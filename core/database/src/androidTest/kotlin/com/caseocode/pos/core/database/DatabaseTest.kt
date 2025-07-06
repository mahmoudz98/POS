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
package com.caseocode.pos.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.casecode.pos.core.database.PosDatabase
import com.casecode.pos.core.database.dao.BusinessDao
import org.junit.After
import org.junit.Before

internal abstract class DatabaseTest {
    private lateinit var db: PosDatabase
    protected lateinit var businessDao: BusinessDao

    @Before
    fun setup() {
        db = run {
            val context = ApplicationProvider.getApplicationContext<Context>()
            Room.inMemoryDatabaseBuilder(
                context,
                PosDatabase::class.java,
            ).build()
        }
        businessDao = db.businessDao()
    }

    @After
    fun teardown() = db.close()
}