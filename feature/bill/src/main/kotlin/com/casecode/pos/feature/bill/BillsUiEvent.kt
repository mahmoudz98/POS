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
package com.casecode.pos.feature.bill

sealed interface BillsUiEvent {
    data object SearchClicked : BillsUiEvent
    data class SearchQueryChanged(val query: String) : BillsUiEvent
    data object ClearRecentSearches : BillsUiEvent
    data class SupplierUnselected(val name: String) : BillsUiEvent
    data class SupplierSelected(val name: String) : BillsUiEvent
    data class SortPaymentStatusChanged(val status: PaymentStatusFilter) : BillsUiEvent
    data class SortTypeChanged(val sortType: InvoiceSortType) : BillsUiEvent
    data object RestDefaultFilter : BillsUiEvent
    data object MessageShown : BillsUiEvent
}

sealed interface BillsEffect {
    data class NavigateToDetail(val billId: String) : BillsEffect
    data object NavigateBack : BillsEffect
    data object NavigateToAdd : BillsEffect
}