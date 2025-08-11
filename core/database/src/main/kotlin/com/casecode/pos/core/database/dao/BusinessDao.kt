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
package com.casecode.pos.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.casecode.pos.core.database.model.BusinessEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [BusinessEntity] access
 */
@Dao
interface BusinessDao {
    @Upsert
    suspend fun insertOrReplaceBusiness(business: BusinessEntity)

    @Query("SELECT * FROM business WHERE owner_uid = :ownerUid LIMIT 1")
    fun getBusinessByOwner(ownerUid: String): Flow<BusinessEntity?>
}