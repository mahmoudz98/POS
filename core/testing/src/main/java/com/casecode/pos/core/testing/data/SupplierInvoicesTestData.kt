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
package com.casecode.pos.core.testing.data

import com.casecode.pos.core.model.data.users.DiscountType
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.model.data.users.PaymentDetails
import com.casecode.pos.core.model.data.users.PaymentMethod
import com.casecode.pos.core.model.data.users.PaymentStatus
import com.casecode.pos.core.model.data.users.SupplierInvoice
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

val supplierInvoicesTestData = listOf(
    SupplierInvoice(
        invoiceId = "INV001",
        billNumber = "BILL001",
        supplierName = "Supplier A",
        // March 15, 2023 12:00:00 AM
        issueDate = Instant.fromEpochMilliseconds(1678886400000),
        // April 15, 2023 12:00:00 AM
        dueDate = Instant.fromEpochMilliseconds(1681574400000),
        createdBy = "AdminUser1",
        subTotal = 1400.0,
        totalAmount = 1300.0,
        discountType = DiscountType.PERCENTAGE,
        amountDiscounted = 100.0,
        paymentStatus = PaymentStatus.PAID,
        paymentDetails = listOf(
            PaymentDetails(
                paymentId = "PAY001",
                // March 15, 2023 1:00:00 AM
                paymentDate = Instant.fromEpochMilliseconds(
                    1678890000000,
                ),
                createdBy = "AdminUser1",
                paymentMethod = PaymentMethod.CASH,
                amountPaid = 500.0,
            ),
        ),
        invoiceItems = listOf(
            Item(name = "Item A", quantity = 2, unitPrice = 500.0),
            Item(name = "Item B", quantity = 1, unitPrice = 400.0),
        ),
    ),
    SupplierInvoice(
        invoiceId = "INV002",
        billNumber = "BILL002",
        supplierName = "Supplier B",
        // March 16, 2023 12:00:00 AM
        issueDate = Instant.fromEpochMilliseconds(1678972800000),
        // April 16, 2023 12:00:00 AM
        dueDate = Instant.fromEpochMilliseconds(1681660800000),
        createdBy = "AdminUser2",
        subTotal = 1000.0,
        totalAmount = 1000.0,
        discountType = DiscountType.FIXED,
        amountDiscounted = 0.0,
        paymentStatus = PaymentStatus.PENDING,
        paymentDetails = emptyList(),
        invoiceItems = listOf(
            Item(name = "Item C", quantity = 5, unitPrice = 200.0),
        ),
    ),
    SupplierInvoice(
        invoiceId = "INV003",
        billNumber = "BILL003",
        supplierName = "Supplier A",
        issueDate = Clock.System.now(),
        dueDate = Clock.System.now().plus(Duration.parse("20d")),
        createdBy = "AdminUser1",
        subTotal = 700.0,
        totalAmount = 750.0,
        discountType = DiscountType.PERCENTAGE,
        amountDiscounted = 50.0,
        paymentStatus = PaymentStatus.PARTIALLY_PAID,
        paymentDetails = listOf(
            PaymentDetails(
                paymentId = "PAY002",
                paymentDate = Clock.System.now(),
                createdBy = "AdminUser1",
                paymentMethod = PaymentMethod.CREDIT_CARD,
                amountPaid = 300.0,
            ),
        ),
        invoiceItems = listOf(
            Item(name = "Item A", quantity = 1, unitPrice = 250.0),
            Item(name = "Item B", quantity = 2, unitPrice = 250.0),
        ),
    ),
    SupplierInvoice(
        invoiceId = "INV004",
        billNumber = "BILL004",
        supplierName = "Supplier C",
        // March 17, 2023 12:00:00 AM
        issueDate = Instant.fromEpochMilliseconds(1679059200000),
        // April 17, 2023 12:00:00 AM
        dueDate = Instant.fromEpochMilliseconds(1681747200000),
        createdBy = "AdminUser3",
        subTotal = 2000.0,
        totalAmount = 2200.0,
        discountType = DiscountType.FIXED,
        amountDiscounted = 200.0,
        paymentStatus = PaymentStatus.PAID,
        paymentDetails = listOf(
            PaymentDetails(
                paymentId = "PAY003",
                // March 17, 2023 1:00:00 AM
                paymentDate = Instant.fromEpochMilliseconds(
                    1679062800000,
                ),
                createdBy = "AdminUser3",
                paymentMethod = PaymentMethod.DIGITAL_PAYMENT,
                amountPaid = 2200.0,
            ),
        ),
        invoiceItems = listOf(
            Item(name = "Item D", quantity = 2, unitPrice = 1000.0),
            Item(name = "Item E", quantity = 1, unitPrice = 200.0),
        ),
    ),
)