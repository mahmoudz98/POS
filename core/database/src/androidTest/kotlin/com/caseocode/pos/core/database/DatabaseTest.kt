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
import com.casecode.pos.core.database.dao.BranchDao
import com.casecode.pos.core.database.dao.BusinessDao
import com.casecode.pos.core.database.dao.EmployeeDao
import com.casecode.pos.core.database.dao.LocalSignalDao
import com.casecode.pos.core.database.dao.OutboxCommandDao
import com.casecode.pos.core.database.dao.SubscriptionDao
import com.casecode.pos.core.database.dao.TaxRateDao
import org.junit.After
import org.junit.Before

internal abstract class DatabaseTest {
    private lateinit var db: PosDatabase

    protected lateinit var branchDao: BranchDao
    protected lateinit var businessDao: BusinessDao
    protected lateinit var employeeDao: EmployeeDao
    protected lateinit var localSignalDao: LocalSignalDao
    protected lateinit var outboxCommandDao: OutboxCommandDao
    protected lateinit var subscriptionDao: SubscriptionDao
    protected lateinit var taxRateDao: TaxRateDao

    @Before
    fun setup() {
        db = run {
            val context = ApplicationProvider.getApplicationContext<Context>()
            Room.inMemoryDatabaseBuilder(
                context,
                PosDatabase::class.java,
            ).build()
        }
        branchDao = db.branchDao()
        businessDao = db.businessDao()
        employeeDao = db.employeeDao()
        localSignalDao = db.localSignalDao()
        outboxCommandDao = db.outboxCommandDao()
        subscriptionDao = db.subscriptionDao()
        taxRateDao = db.taxRateDao()
    }

    @After
    fun teardown() = db.close()
}
