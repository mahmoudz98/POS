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
import com.casecode.pos.core.database.model.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [SubscriptionEntity] access.
 */
@Dao
interface SubscriptionDao {
    /**
     * Inserts or replaces a [SubscriptionEntity] in the database.
     * @param subscription The [SubscriptionEntity] to insert or replace.
     */
    @Upsert
    suspend fun insertOrReplaceSubscription(subscription: SubscriptionEntity)

    /**
     * Retrieves the current [SubscriptionEntity] as a [Flow].
     * @return A [Flow] emitting the [SubscriptionEntity] or null if not found.
     */
    @Query("SELECT * FROM subscription LIMIT 1")
    fun getSubscription(): Flow<SubscriptionEntity?>
}