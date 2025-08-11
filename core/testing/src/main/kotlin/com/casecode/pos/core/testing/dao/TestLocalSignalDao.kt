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

import com.casecode.pos.core.database.dao.LocalSignalDao
import com.casecode.pos.core.database.model.LocalSignalEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TestLocalSignalDao : LocalSignalDao {

    private val signalsFlow = MutableStateFlow<List<LocalSignalEntity>>(emptyList())
    val insertedSignals = mutableListOf<LocalSignalEntity>()

    override suspend fun insertSignal(signal: LocalSignalEntity) {
        insertedSignals.add(signal)
        signalsFlow.value = insertedSignals
    }

    override fun getPendingSignals(): Flow<List<LocalSignalEntity>> {
        return signalsFlow.asStateFlow()
    }

    override suspend fun updateSignalStatus(id: String, status: Int) {
        val signalToUpdate = insertedSignals.find { it.id == id }
        if (signalToUpdate != null) {
            val updatedSignal = signalToUpdate.copy(status = status)
            insertedSignals.remove(signalToUpdate)
            insertedSignals.add(updatedSignal)
            signalsFlow.value = insertedSignals
        }
    }

    override suspend fun deleteSignal(id: String) {
        insertedSignals.removeAll { it.id == id }
        signalsFlow.value = insertedSignals
    }
}