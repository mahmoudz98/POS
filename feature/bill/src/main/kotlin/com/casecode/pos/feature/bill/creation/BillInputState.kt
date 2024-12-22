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
package com.casecode.pos.feature.bill.creation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.casecode.pos.core.model.data.users.DiscountType
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.model.data.users.SupplierInvoice
import kotlinx.datetime.Clock.System
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@Stable
class BillInputState(
    billUpdated: SupplierInvoice? = null,
) {
    var supplierName by mutableStateOf("")
    var billNumber by mutableStateOf("")
    var issueDate by mutableStateOf(System.now())
    var dueDate by mutableStateOf(System.now())
    private val _invoiceItems = SnapshotStateList<Item>()
    val invoiceItems get() = _invoiceItems
    val subTotal: Double
        get() {
            return invoiceItems.sumOf {
                it.costPrice.times(it.quantity.toDouble())
            }
        }
    var discount by mutableStateOf("")
    var discountTypeCurrency by mutableStateOf(DiscountType.PERCENTAGE)

    val total: Double
        get() {
            val discountValue = discount.toDoubleOrNull()
            return if (discountValue != null) {
                if (discountTypeCurrency == DiscountType.FIXED) {
                    subTotal - discountValue
                } else {
                    subTotal * (1 - discountValue / 100)
                }
            } else {
                subTotal
            }
        }

    var supplierNameError by mutableStateOf(false)
    var billNumberError by mutableStateOf(false)

    init {
        billUpdated?.let { bill ->
            supplierName = bill.supplierName
            billNumber = bill.billNumber
            issueDate = bill.issueDate
            dueDate = bill.dueDate
            invoiceItems.addAll(bill.invoiceItems)
        }
    }

    fun onSupplierNameChange(input: String) {
        supplierName = input
        supplierNameError = input.isEmpty()
    }

    fun onBillNumberChange(input: String) {
        billNumber = input
        billNumberError = input.isEmpty()
    }

    fun onIssueDateChange(input: Long) {
        input.let { newIssueDate ->
            // Update issue date
            issueDate = Instant.fromEpochMilliseconds(input)

            updateDueDateIfIssueDateLater(newIssueDate)
        }
    }

    private fun updateDueDateIfIssueDateLater(newIssueDate: Long) {
        val newIssueDateStart = millisToStartOfDay(newIssueDate)
        val currentDueDateStart = millisToStartOfDay(dueDate.toEpochMilliseconds())
        if (newIssueDateStart > currentDueDateStart) {
            onDueDateChange(newIssueDate)
        }
    }

    fun onDueDateChange(input: Long) {
        dueDate = Instant.fromEpochMilliseconds(input)
    }

    internal fun addItem(item: Item) {
        _invoiceItems.add(item)
    }

    internal fun updateItem(item: Item) {
        val existingItem = _invoiceItems.find { it.sku == item.sku } ?: return
        _invoiceItems.apply {
            remove(existingItem)
            add(item)
        }
    }

    internal fun removeItem(item: Item) {
        _invoiceItems.remove(item)
    }

    fun onDiscountChange(input: String) {
        discount = input
    }

    fun onDiscountTypeChange() {
        discountTypeCurrency =
            if (discountTypeCurrency == DiscountType.PERCENTAGE) {
                DiscountType.FIXED
            } else {
                DiscountType.PERCENTAGE
            }
    }
}

internal fun millisToStartOfDay(timeMillis: Long): Long {
    return Instant.fromEpochMilliseconds(timeMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
        .atStartOfDayIn(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()
}

internal fun millisToStartOfDay(timeMillis: Instant): Long {
    return timeMillis
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
        .atStartOfDayIn(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()
}