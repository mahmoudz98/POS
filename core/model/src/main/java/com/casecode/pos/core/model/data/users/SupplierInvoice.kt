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
package com.casecode.pos.core.model.data.users

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class SupplierInvoice(
    val invoiceId: String = "",
    val billNumber: String = "",
    val supplierName: String = "",
    val issueDate: Instant = Clock.System.now(),
    val dueDate: Instant = Clock.System.now(),
    val createdBy: String = "",
    val subTotal: Double = 0.0,
    val totalAmount: Double = 0.0,
    val discountType: DiscountType = DiscountType.PERCENTAGE,
    val amountDiscounted: Double = 0.0,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val paymentDetails: List<PaymentDetails> = emptyList(),
    val invoiceItems: List<Item> = emptyList(),
) {
    val isPaid: Boolean
        get() = paymentStatus == PaymentStatus.PAID
    val dueAmount: Double
        get() = totalAmount - paymentDetails.sumOf { it.amountPaid }
}

enum class DiscountType {
    PERCENTAGE,
    FIXED,
}
enum class PaymentStatus {
    PENDING,
    PAID,
    PARTIALLY_PAID,
    OVERDUE,
}

data class PaymentDetails(
    val paymentId: String = "",
    val paymentDate: Instant = Clock.System.now(),
    val createdBy: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val amountPaid: Double = 0.0,
)

enum class PaymentMethod {
    CASH,
    CREDIT_CARD,
    DIGITAL_PAYMENT,
}