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

import com.casecode.pos.core.database.dao.EmployeeDao
import com.casecode.pos.core.database.model.EmployeeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestEmployeeDao : EmployeeDao {

    private val entitiesStateFlow = MutableStateFlow<List< EmployeeEntity>>(emptyList())

    override suspend fun insertOrReplaceEmployee(employee: EmployeeEntity) {
        entitiesStateFlow.update {
            it + (employee)
        }
    }

    override suspend fun updateEmployees(vararg employees: EmployeeEntity) {
        entitiesStateFlow.update { old ->
            val employeesMap = employees.associateBy { it.id }
            old.map { existingEmployee ->
                employeesMap[existingEmployee.id] ?: existingEmployee
            }
        }
    }

    override fun getEmployees(): Flow<List<EmployeeEntity>> {
        return entitiesStateFlow.map { it.filter { entity -> entity.isDeleted == 0 } }
    }

    override suspend fun getEmployeeById(id: String): EmployeeEntity? {
        return entitiesStateFlow.value.firstOrNull { it.id == id }
    }

    override suspend fun getEmployeeByName(name: String): EmployeeEntity? {
        return entitiesStateFlow.value.firstOrNull { it.name == name }
    }

    override suspend fun deleteEmployee(id: String) {

    }
        }
