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
package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.repository.SupplierRepository
import com.casecode.pos.core.model.data.users.Supplier
import javax.inject.Inject

class GetSuppliersUseCase @Inject constructor(private val supplierRepository: SupplierRepository) {
    operator fun invoke() = supplierRepository.getSuppliers()
}

class AddSupplierUseCase @Inject constructor(private val supplierRepository: SupplierRepository) {
    suspend operator fun invoke(supplier: Supplier) = supplierRepository.addSupplier(supplier)
}

class UpdateSupplierUseCase @Inject constructor(private val supplierRepository: SupplierRepository) {
    suspend operator fun invoke(oldSupplier: Supplier, newSupplier: Supplier) =
        supplierRepository.updateSupplier(oldSupplier, newSupplier)
}

class DeleteSupplierUseCase @Inject constructor(private val supplierRepository: SupplierRepository) {
    suspend operator fun invoke(supplier: Supplier) = supplierRepository.deleteSupplier(supplier)
}