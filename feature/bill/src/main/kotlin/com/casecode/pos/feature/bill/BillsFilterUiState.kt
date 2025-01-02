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

import androidx.annotation.StringRes
import kotlinx.datetime.Instant

data class BillsFilterUiState(
    val selectedSuppliers: Set<String> = emptySet(),
    val paymentStatusFilter: PaymentStatusFilter = PaymentStatusFilter.All,
    val sortType: InvoiceSortType = InvoiceSortType.DateNewToOld,
    val dateRange: DateRange? = null,
)

enum class PaymentStatusFilter(@StringRes val labelId: Int) {
    All(R.string.feature_bill_filter_all_label),
    Paid(R.string.feature_bill_filter_paid_label),
    Pending(R.string.feature_bill_filter_pending_label),
    PartiallyPaid(R.string.feature_bill_filter_partially_paid_label),
    Overdue(R.string.feature_bill_filter_overdue_label),
}

enum class InvoiceSortType(@StringRes val labelId: Int) {
    DateNewToOld(R.string.feature_bill_sort_new_to_old_label),
    DateOldToNew(R.string.feature_bill_sort_old_to_new_label),
    AmountHighToLow(R.string.feature_bill_sort_high_to_low_label),
    AmountLowToHigh(R.string.feature_bill_sort_low_to_high_label),
}

data class DateRange(
    val startDate: Instant,
    val endDate: Instant,
)