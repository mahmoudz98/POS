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
import androidx.room.Query
import androidx.room.Update
import com.casecode.pos.core.database.model.EmployeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Insert
    suspend fun insertOrReplaceEmployee(employee: EmployeeEntity)

    @Update
    suspend fun updateEmployees(vararg employees: EmployeeEntity)

    @Query("SELECT * FROM employee WHERE is_deleted = 0")
    fun getEmployees(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employee WHERE id = :id and is_deleted = 0")
    suspend fun getEmployeeById(id: String): EmployeeEntity?

    @Query("SELECT * FROM employee WHERE name = :name and is_deleted = 0")
    suspend fun getEmployeeByName(name: String): EmployeeEntity?

    @Query("UPDATE employee SET is_deleted = 1 WHERE id = :id")
    suspend fun deleteEmployee(id: String)
}
