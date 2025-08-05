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
package com.casecode.pos.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.casecode.pos.core.database.dao.BranchDao
import com.casecode.pos.core.database.dao.BusinessDao
import com.casecode.pos.core.database.dao.LocalSignalDao
import com.casecode.pos.core.database.dao.OutboxCommandDao
import com.casecode.pos.core.database.dao.SubscriptionDao
import com.casecode.pos.core.database.dao.TaxRateDao
import com.casecode.pos.core.database.model.BranchEntity
import com.casecode.pos.core.database.model.BusinessEntity
import com.casecode.pos.core.database.model.LocalSignalEntity
import com.casecode.pos.core.database.model.OutboxCommandEntity
import com.casecode.pos.core.database.model.SubscriptionEntity
import com.casecode.pos.core.database.model.TaxRateEntity
import com.casecode.pos.core.database.util.BigDecimalConverter
import com.casecode.pos.core.database.util.InstantConverter
import com.casecode.pos.core.database.util.StringListConverter

@Database(
    entities = [
        BusinessEntity::class,
        BranchEntity::class,
        SubscriptionEntity::class,
        TaxRateEntity::class,
        OutboxCommandEntity::class,
        LocalSignalEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
    StringListConverter::class,
    BigDecimalConverter::class,
)
abstract class PosDatabase : RoomDatabase() {
    abstract fun businessDao(): BusinessDao
    abstract fun branchDao(): BranchDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun taxRateDao(): TaxRateDao
    abstract fun outboxCommandDao(): OutboxCommandDao
    abstract fun localSignalDao(): LocalSignalDao
}