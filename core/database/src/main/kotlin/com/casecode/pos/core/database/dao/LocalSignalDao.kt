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
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.casecode.pos.core.database.model.LocalSignalEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for managing the local signals queue.
 */
@Dao
interface LocalSignalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSignal(signal: LocalSignalEntity)

    @Query("SELECT * FROM local_signals WHERE status = 0 ORDER BY createdAt ASC")
    fun getPendingSignals(): Flow<List<LocalSignalEntity>>

    @Query("UPDATE local_signals SET status = :status WHERE id = :id")
    suspend fun updateSignalStatus(id: String, status: Int)

    @Query("DELETE FROM local_signals WHERE id = :id")
    suspend fun deleteSignal(id: String)
}