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
import com.casecode.pos.core.database.model.OutboxCommandEntity
import com.casecode.pos.core.database.model.OutboxCommandStatus
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

/**
 * DAO for [OutboxCommandEntity] access, supporting the Outbox Pattern for reliable data sync.
 */
@Dao
interface OutboxCommandDao {
    /**
     * Inserts a new command into the outbox.
     *
     * @param command The [OutboxCommandEntity] to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommand(command: OutboxCommandEntity)

    /**
     * Retrieves all pending or processing commands from the outbox, ordered by creation time.
     * The worker will use this to find tasks to execute.
     *
     * @return A [Flow] emitting a list of pending [OutboxCommandEntity].
     */
    @Query(
        """
        SELECT * FROM outbox_commands
        WHERE status = :pendingStatus OR status = :processingStatus
        ORDER BY created_at ASC
        """,
    )
    fun getPendingCommands(
        pendingStatus: Int = OutboxCommandStatus.PENDING,
        processingStatus: Int = OutboxCommandStatus.PROCESSING,
    ): Flow<List<OutboxCommandEntity>>

    /**
     * Updates the status of a specific command after a sync attempt.
     *
     * @param id The ID of the command to update.
     * @param status The new status of the command.
     * @param attemptCount  the number of attempt
     * @param lastAttemptedAt The timestamp of this attempt.
     * @param errorMessage An optional error message if the attempt failed.
     */
    @Query(
        """
        UPDATE outbox_commands
        SET status = :status, last_attempted_at = :lastAttemptedAt, last_error = :errorMessage, attempt_count = :attemptCount + 1
        WHERE id = :id
        """,
    )
    suspend fun updateCommandStatus(
        id: Int,
        status: Int,
        attemptCount: Int,
        lastAttemptedAt: Instant,
        errorMessage: String? = null,
    )

    /**
     * Deletes a command from the outbox, typically after it has been successfully completed.
     *
     * @param id The ID of the command to delete.
     */
    @Query("DELETE FROM outbox_commands WHERE id = :id")
    suspend fun deleteCommand(id: Int)

    /**
     * Deletes all completed commands from the outbox to keep the table clean.
     */
    @Query("DELETE FROM outbox_commands WHERE status = :completedStatus")
    suspend fun deleteCompletedCommands(completedStatus: Int = OutboxCommandStatus.COMPLETED)
}