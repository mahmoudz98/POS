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
import androidx.room.Update
import com.casecode.pos.core.database.model.EmployeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Query("SELECT * FROM employees WHERE is_active = 0 ")
    fun getEmployees(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE business_id = :businessId   AND id = :identifier  LIMIT 1")
    suspend fun getEmployeeById(identifier: String, businessId: String): EmployeeEntity?

    @Query("SELECT MAX(CAST(id AS INTEGER)) FROM employees ")
    suspend fun getHighestEmployeeId(): Int?

    @Query("SELECT EXISTS(SELECT 1 FROM employees WHERE id = :id)")
    suspend fun employeeExists(id: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrReplaceEmployee(employee: EmployeeEntity)

    @Update
    suspend fun updateEmployees(vararg employees: EmployeeEntity)

    @Query("UPDATE employees SET is_active = 1 WHERE id = :id AND business_id = :businessId")
    suspend fun deleteEmployee(id: String, businessId: String)
}
