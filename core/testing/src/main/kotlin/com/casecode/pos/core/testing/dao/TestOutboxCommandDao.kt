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
package com.casecode.pos.core.testing.dao

import com.casecode.pos.core.database.dao.OutboxCommandDao
import com.casecode.pos.core.database.model.OutboxCommandEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlin.time.Instant

class TestOutboxCommandDao : OutboxCommandDao {

    private val commandsFlow = MutableStateFlow<List<OutboxCommandEntity>>(emptyList())
    val insertedCommands = mutableListOf<OutboxCommandEntity>()

    override fun getPendingCommands(pendingStatus: Int, processingStatus: Int): Flow<List<OutboxCommandEntity>> {
        return commandsFlow.asStateFlow().map { commands ->
            commands.filter { it.status == pendingStatus || it.status == processingStatus }
        }
    }

    override suspend fun insertCommand(command: OutboxCommandEntity) {
        insertedCommands.add(command)
        commandsFlow.value = insertedCommands.toList()
    }

    override suspend fun updateCommandStatus(id: Int, status: Int, attemptCount: Int, lastAttemptedAt: Instant, errorMessage: String?) {
        val commandToUpdate = insertedCommands.find { it.id == id }
        if (commandToUpdate != null) {
            val updatedCommand = commandToUpdate.copy(
                status = status,
                attemptCount = commandToUpdate.attemptCount + 1,
                lastAttemptedAt = lastAttemptedAt,
                lastError = errorMessage,
            )
            insertedCommands.remove(commandToUpdate)
            insertedCommands.add(updatedCommand)
            commandsFlow.value = insertedCommands.toList()
        }
    }

    override suspend fun deleteCommand(id: Int) {
        insertedCommands.removeAll { it.id == id }
        commandsFlow.value = insertedCommands.toList()
    }

    override suspend fun deleteCompletedCommands(completedStatus: Int) {
        insertedCommands.removeAll { it.status == completedStatus }
        commandsFlow.value = insertedCommands.toList()
    }
}