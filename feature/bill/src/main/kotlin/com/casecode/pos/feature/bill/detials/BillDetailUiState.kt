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
package com.casecode.pos.feature.bill.detials

import com.casecode.pos.core.model.data.users.SupplierInvoice

sealed interface BillDetailUiState {
    data object Loading : BillDetailUiState
    data object Error : BillDetailUiState
    data object EmptySelection : BillDetailUiState
    data class Success(val supplierInvoice: SupplierInvoice) : BillDetailUiState
}