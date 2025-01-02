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
package com.casecode.pos.core.domain.repository

import com.casecode.pos.core.domain.utils.OperationResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Supplier
import kotlinx.coroutines.flow.Flow

interface SupplierRepository {
    fun getSuppliers(): Flow<Resource<List<Supplier>>>
    suspend fun addSupplier(supplier: Supplier): OperationResult
    suspend fun updateSupplier(oldSupplier: Supplier, newSupplier: Supplier): OperationResult
    suspend fun deleteSupplier(supplier: Supplier): OperationResult
}