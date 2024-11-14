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

import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.model.data.users.PaymentStatus
import com.casecode.pos.core.model.data.users.SupplierInvoice
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

val supplierInvoicesTestData = listOf(
    SupplierInvoice(
        invoiceId = "INV001",
        supplierId = "SUP001",
        // March 15, 2023 12:00:00 AM
        createdAt = Instant.fromEpochMilliseconds(1678886400000),
        createdBy = "user1",
        totalAmount = 1500.0,
        paymentStatus = PaymentStatus.PAID,
        invoiceItems = listOf(
            Item(name = "Item 1", quantity = 2, unitPrice = 500.0),
            Item(name = "Item 2", quantity = 1, unitPrice = 500.0),
        ),
    ),

    SupplierInvoice(
        invoiceId = "INV002",
        supplierId = "SUP002",
        // March 16, 2023 12:00:00 AM
        createdAt = Instant.fromEpochMilliseconds(1678972800000),
        createdBy = "user2",
        totalAmount = 1000.0,
        paymentStatus = PaymentStatus.PENDING,
        invoiceItems = listOf(
            Item(name = "Item 3", quantity = 5, unitPrice = 200.0),
        ),
    ),
    SupplierInvoice(
        invoiceId = "INV003",
        supplierId = "SUP001",
        createdAt = Clock.System.now(),
        createdBy = "user1",
        totalAmount = 750.0,
        paymentStatus = PaymentStatus.PARTIALLY_PAID,
        invoiceItems = listOf(
            Item(name = "Item 1", quantity = 1, unitPrice = 250.0),
            Item(name = "Item 2", quantity = 2, unitPrice = 250.0),
        ),
    ),
    SupplierInvoice(
        invoiceId = "INV004",
        supplierId = "SUP003",
        // March 17, 2023 12:00:00 AM
        createdAt = Instant.fromEpochMilliseconds(1679059200000),
        createdBy = "user3",
        totalAmount = 2200.0,
        paymentStatus = PaymentStatus.PAID,
        invoiceItems = listOf(
            Item(name = "Item 4", quantity = 2, unitPrice = 1000.0),
            Item(name = "Item 5", quantity = 1, unitPrice = 200.0),
        ),
    ),
    SupplierInvoice(
        invoiceId = "INV005",
        supplierId = "SUP002",
        // March 18, 2023 12:00:00 AM
        createdAt = Instant.fromEpochMilliseconds(1679145600000),
        createdBy = "user2",
        totalAmount = 1850.0,
        paymentStatus = PaymentStatus.PENDING,
        invoiceItems = listOf(
            Item(name = "Item 3", quantity = 3, unitPrice = 500.0),
            Item(name = "Item 6", quantity = 2, unitPrice = 175.0),
        ),
    ),

)