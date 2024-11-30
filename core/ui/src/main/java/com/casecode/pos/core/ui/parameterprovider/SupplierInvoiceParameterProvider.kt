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
package com.casecode.pos.core.ui.parameterprovider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.casecode.pos.core.model.data.users.DiscountType
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.model.data.users.PaymentDetails
import com.casecode.pos.core.model.data.users.PaymentMethod
import com.casecode.pos.core.model.data.users.PaymentStatus
import com.casecode.pos.core.model.data.users.SupplierInvoice
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class SupplierInvoiceParameterProvider : PreviewParameterProvider<List<SupplierInvoice>> {
    override val values: Sequence<List<SupplierInvoice>>
        get() =
            sequenceOf(
                listOf(
                    SupplierInvoice(
                        invoiceId = "INV001",
                        billNumber = "12345324234",
                        supplierName = "Supplier A",
                        issueDate = Clock.System.now(),
                        dueDate = Clock.System.now(),
                        paymentStatus = PaymentStatus.PENDING,
                        subTotal = 200.0,
                        totalAmount = 100.0,
                        discountType = DiscountType.PERCENTAGE,
                        amountDiscounted = 50.0,
                        paymentDetails = emptyList(),
                        invoiceItems = listOf(
                            Item(name = "Item 1", quantity = 10, costPrice = 10.0),
                        ),
                    ),
                    SupplierInvoice(
                        invoiceId = "INV002",
                        billNumber = "67842342334290",
                        supplierName = "Supplier B",
                        issueDate = Clock.System.now(),
                        dueDate = Clock.System.now(),
                        paymentStatus = PaymentStatus.PAID,
                        totalAmount = 100.0,
                        paymentDetails = listOf(
                            PaymentDetails(
                                paymentId = "1",
                                paymentDate = Clock.System.now(),
                                createdBy = "User A",
                                paymentMethod = PaymentMethod.CASH,
                                amountPaid = 100.0,
                            ),
                            PaymentDetails(
                                paymentId = "12",
                                paymentDate = Clock.System.now(),
                                createdBy = "User B",
                                paymentMethod = PaymentMethod.CASH,
                                amountPaid = 10.0,
                            ),
                        ),
                        invoiceItems = listOf(
                            Item(name = "Item 1", quantity = 10, costPrice = 10.0),
                        ),
                    ),
                    SupplierInvoice(
                        invoiceId = "INV003",
                        billNumber = "13572349",
                        supplierName = "Supplier C",
                        issueDate = Clock.System.now(),
                        dueDate = Clock.System.now(),
                        paymentStatus = PaymentStatus.PARTIALLY_PAID,
                        totalAmount = 100.0,
                        paymentDetails = listOf(
                            PaymentDetails(
                                paymentId = "PAY002",
                                paymentDate = Clock.System.now(),
                                createdBy = "User B",
                                paymentMethod = PaymentMethod.CREDIT_CARD,
                                amountPaid = 50.0,
                            ),
                        ),
                        invoiceItems = listOf(
                            Item(name = "Item 1", quantity = 10, costPrice = 10.0),
                        ),
                    ),
                    SupplierInvoice(
                        invoiceId = "INV004",
                        billNumber = "2460",
                        supplierName = "Supplier D",
                        issueDate = Clock.System.now(),
                        dueDate = Clock.System.now(),
                        paymentStatus = PaymentStatus.PAID,
                        totalAmount = 100.0,
                        paymentDetails = emptyList(),
                        invoiceItems = emptyList(),
                    ),
                    SupplierInvoice(
                        invoiceId = "INV005",
                        billNumber = "11223",
                        supplierName = "Supplier E",
                        // March 18, 2023 12:00:00 AM
                        issueDate = Instant.fromEpochMilliseconds(1679145600000),
                        // e.g., 7 days later
                        dueDate = Instant.fromEpochMilliseconds(1679750400000),
                        paymentStatus = PaymentStatus.OVERDUE,
                        totalAmount = 100.0,
                        paymentDetails = emptyList(),
                        invoiceItems = listOf(
                            Item(name = "Item 1", quantity = 10, costPrice = 10.0),
                        ),
                    ),
                ),

            )
}