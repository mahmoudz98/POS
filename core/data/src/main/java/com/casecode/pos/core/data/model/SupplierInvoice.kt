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
package com.casecode.pos.core.data.model

import com.casecode.pos.core.firebase.services.ITEM_COST_PRICE_FIELD
import com.casecode.pos.core.firebase.services.ITEM_NAME_FIELD
import com.casecode.pos.core.firebase.services.ITEM_QUANTITY_FIELD
import com.casecode.pos.core.firebase.services.ITEM_SKU_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_AMOUNT_DISCOUNTED_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_BILL_NUMBER_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_CREATED_BY_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_DISCOUNT_TYPE_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_DUE_DATE_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_ID_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_ISSUE_DATE_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_ITEMS_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_PAYMENT_DETAILS_AMOUNT_PAID_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_PAYMENT_DETAILS_CREATED_BY_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_PAYMENT_DETAILS_DATE_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_PAYMENT_DETAILS_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_PAYMENT_DETAILS_ID_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_PAYMENT_DETAILS_METHOD_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_PAYMENT_STATUS_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_SUPPLIER_ID_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_TOTAL_AMOUNT_FIELD
import com.casecode.pos.core.model.data.users.DiscountType
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.model.data.users.PaymentDetails
import com.casecode.pos.core.model.data.users.PaymentMethod
import com.casecode.pos.core.model.data.users.PaymentStatus
import com.casecode.pos.core.model.data.users.SupplierInvoice
import kotlinx.datetime.Instant

internal fun asExternalMapper(docId: String, invoice: SupplierInvoice): Map<String?, Any> = mapOf(
    SUPPLIER_INVOICE_ID_FIELD to docId,
    SUPPLIER_INVOICE_BILL_NUMBER_FIELD to invoice.billNumber,
    SUPPLIER_INVOICE_SUPPLIER_ID_FIELD to invoice.supplierName,
    SUPPLIER_INVOICE_ISSUE_DATE_FIELD to invoice.issueDate.toEpochMilliseconds(),
    SUPPLIER_INVOICE_DUE_DATE_FIELD to invoice.dueDate.toEpochMilliseconds(),
    SUPPLIER_INVOICE_CREATED_BY_FIELD to invoice.createdBy,
    SUPPLIER_INVOICE_TOTAL_AMOUNT_FIELD to invoice.totalAmount,
    SUPPLIER_INVOICE_DISCOUNT_TYPE_FIELD to invoice.discountType.name,
    SUPPLIER_INVOICE_AMOUNT_DISCOUNTED_FIELD to invoice.amountDiscounted,
    SUPPLIER_INVOICE_PAYMENT_STATUS_FIELD to invoice.paymentStatus.name,
    SUPPLIER_INVOICE_PAYMENT_DETAILS_FIELD to invoice.paymentDetails
        .map { payment ->
            payment.asExternalMapper()
        },
    SUPPLIER_INVOICE_ITEMS_FIELD to invoice.invoiceItems.map { item ->
        item.asExternalMapper()
    },
)

@Suppress("UNCHECKED_CAST")
internal fun Map<String, Any>.asDomainModel(): SupplierInvoice {
    return SupplierInvoice(
        invoiceId = this[SUPPLIER_INVOICE_ID_FIELD] as? String ?: "",
        billNumber = this[SUPPLIER_INVOICE_BILL_NUMBER_FIELD] as? String ?: "",
        supplierName = this[SUPPLIER_INVOICE_SUPPLIER_ID_FIELD] as? String ?: "",
        issueDate = Instant.fromEpochMilliseconds(this[SUPPLIER_INVOICE_ISSUE_DATE_FIELD] as Long),
        dueDate = Instant.fromEpochMilliseconds(this[SUPPLIER_INVOICE_DUE_DATE_FIELD] as Long),
        createdBy = this[SUPPLIER_INVOICE_CREATED_BY_FIELD] as? String ?: "",
        totalAmount = this[SUPPLIER_INVOICE_TOTAL_AMOUNT_FIELD] as? Double ?: 0.0,
        discountType = DiscountType.valueOf(
            this[SUPPLIER_INVOICE_DISCOUNT_TYPE_FIELD] as? String ?: "",
        ),
        amountDiscounted = this[SUPPLIER_INVOICE_AMOUNT_DISCOUNTED_FIELD] as? Double ?: 0.0,
        paymentStatus = PaymentStatus.valueOf(
            this[SUPPLIER_INVOICE_PAYMENT_STATUS_FIELD] as String,
        ),
        paymentDetails = (get(SUPPLIER_INVOICE_PAYMENT_DETAILS_FIELD) as? List<Map<String, Any>>)
            ?.mapNotNull { map ->
                map.asDomainPaymentDetailsModel()
            } ?: emptyList(),
        invoiceItems = (get(SUPPLIER_INVOICE_ITEMS_FIELD) as? List<Map<String, Any>>)
            ?.mapNotNull { map ->
                map.asDomainItemModel()
            } ?: emptyList(),
    )
}

internal fun Map<String, Any>.asDomainPaymentDetailsModel(): PaymentDetails? {
    return try {
        PaymentDetails(
            paymentId = this[SUPPLIER_INVOICE_PAYMENT_DETAILS_ID_FIELD] as? String ?: "",
            amountPaid = (this[SUPPLIER_INVOICE_PAYMENT_DETAILS_AMOUNT_PAID_FIELD] as? Double)
                ?: 0.0,
            paymentMethod = PaymentMethod.valueOf(
                this[SUPPLIER_INVOICE_PAYMENT_DETAILS_METHOD_FIELD] as? String ?: "",
            ),
            createdBy = this[SUPPLIER_INVOICE_PAYMENT_DETAILS_CREATED_BY_FIELD] as? String ?: "",
            paymentDate = Instant.fromEpochMilliseconds(
                this[SUPPLIER_INVOICE_PAYMENT_DETAILS_DATE_FIELD] as Long,
            ),
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

internal fun Map<String, Any>.asDomainItemModel(): Item {
    return Item(
        name = this[ITEM_NAME_FIELD] as String,
        sku = this[ITEM_SKU_FIELD] as String,
        quantity = (this[ITEM_QUANTITY_FIELD] as Long).toInt(),
        costPrice = this[ITEM_COST_PRICE_FIELD] as Double,
    )
}

internal fun PaymentDetails.asExternalMapper(): Map<String, Comparable<*>> = mapOf(
    SUPPLIER_INVOICE_PAYMENT_DETAILS_ID_FIELD to this.paymentId,
    SUPPLIER_INVOICE_PAYMENT_DETAILS_DATE_FIELD to this.paymentDate.toEpochMilliseconds(),
    SUPPLIER_INVOICE_PAYMENT_DETAILS_CREATED_BY_FIELD to this.createdBy,
    SUPPLIER_INVOICE_PAYMENT_DETAILS_METHOD_FIELD to this.paymentMethod.name,
    SUPPLIER_INVOICE_PAYMENT_DETAILS_AMOUNT_PAID_FIELD to this.amountPaid,
)