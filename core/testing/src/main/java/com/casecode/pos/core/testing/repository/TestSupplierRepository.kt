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
package com.casecode.pos.core.testing.repository

import com.casecode.pos.core.domain.repository.SupplierRepository
import com.casecode.pos.core.domain.utils.OperationResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Supplier
import com.casecode.pos.core.testing.base.BaseTestRepository
import com.casecode.pos.core.testing.data.suppliersTestData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import com.casecode.pos.core.data.R.string as stringData

class TestSupplierRepository @Inject constructor() :
    BaseTestRepository(),
    SupplierRepository {
    private val resourcesSuppliersFlow: MutableSharedFlow<Resource<List<Supplier>>> =
        MutableSharedFlow(replay = 2, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val suppliersTest = ArrayList(suppliersTestData)

    override fun init() = Unit
    fun sendSuppliers() {
        resourcesSuppliersFlow.tryEmit(Resource.success(suppliersTest))
    }

    override fun setReturnEmpty(value: Boolean) {
        super.setReturnEmpty(value)
        resourcesSuppliersFlow.tryEmit(Resource.empty())
    }

    override fun setReturnError(value: Boolean) {
        super.setReturnError(value)
        resourcesSuppliersFlow.tryEmit(
            Resource.error(stringData.core_data_error_fetching_suppliers),
        )
    }

    override fun getSuppliers(): Flow<Resource<List<Supplier>>> = resourcesSuppliersFlow

    override suspend fun addSupplier(supplier: Supplier): OperationResult {
        if (shouldReturnError) {
            return OperationResult.Failure(stringData.core_data_add_supplier_failure_generic)
        }
        suppliersTest.add(supplier)
        resourcesSuppliersFlow.tryEmit(Resource.success(suppliersTest))
        return OperationResult.Success
    }

    override suspend fun updateSupplier(
        oldSupplier: Supplier,
        newSupplier: Supplier,
    ): OperationResult {
        if (shouldReturnError) {
            return OperationResult.Failure(stringData.core_data_update_supplier_failure_generic)
        }
        suppliersTest.remove(oldSupplier)
        suppliersTest.add(newSupplier)
        resourcesSuppliersFlow.tryEmit(Resource.success(suppliersTest))
        return OperationResult.Success
    }

    override suspend fun deleteSupplier(supplier: Supplier): OperationResult {
        if (shouldReturnError) {
            return OperationResult.Failure(stringData.core_data_delete_supplier_failure_generic)
        }
        suppliersTest.remove(supplier)
        resourcesSuppliersFlow.tryEmit(Resource.success(suppliersTest))
        return OperationResult.Success
    }
}