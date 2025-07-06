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
import com.casecode.pos.core.database.dao.BusinessDao
import com.casecode.pos.core.database.model.BranchEntity
import com.casecode.pos.core.database.model.BusinessEntity
import com.casecode.pos.core.database.util.InstantConverter

@Database(
    entities = [BusinessEntity::class, BranchEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
)
internal abstract class PosDatabase : RoomDatabase() {
    abstract fun businessDao(): BusinessDao
}