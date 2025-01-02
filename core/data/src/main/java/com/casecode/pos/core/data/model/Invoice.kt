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

import com.casecode.pos.core.data.utils.toDateFormatString
import com.casecode.pos.core.firebase.services.INVOICE_CREATED_BY_FIELD
import com.casecode.pos.core.firebase.services.INVOICE_CUSTOMER_FIELD
import com.casecode.pos.core.firebase.services.INVOICE_DATE_FIELD
import com.casecode.pos.core.firebase.services.INVOICE_ITEMS_FIELD
import com.casecode.pos.core.firebase.services.INVOICE_NAME_FIELD
import com.casecode.pos.core.firebase.services.model.InvoiceDataModel
import com.casecode.pos.core.model.data.users.Invoice
import com.casecode.pos.core.model.data.users.InvoiceGroup
import com.google.firebase.firestore.DocumentReference

fun Invoice.asExternalMapper(
    documentRef: DocumentReference,
    createdBy: String,
): Map<String, Any?> {
    val networkInvoice =
        InvoiceDataModel(documentRef.id, this.date, createdBy, this.customer, this.items)
    val invoiceMap =
        mapOf(
            INVOICE_NAME_FIELD to documentRef.id,
            INVOICE_DATE_FIELD to networkInvoice.date,
            INVOICE_CREATED_BY_FIELD to networkInvoice.createdBy,
            INVOICE_CUSTOMER_FIELD to networkInvoice.customer,
            INVOICE_ITEMS_FIELD to networkInvoice.items,
        )
    return invoiceMap
}

fun List<Invoice>.toInvoicesGroup(): List<InvoiceGroup> {
    val invoiceMap = mutableMapOf<String, MutableList<Invoice>>()
    for (invoice in this) {
        val formattedDate = invoice.date.toDateFormatString()
        if (invoiceMap.containsKey(formattedDate).not()) {
            invoiceMap[formattedDate] = mutableListOf()
        }
        invoiceMap[formattedDate]?.add(invoice)
    }
    val invoicesGroup =
        invoiceMap
            .map { invoiceGroup ->
                InvoiceGroup(
                    invoiceGroup.key,
                    invoiceGroup.value,
                )
            }.sortedByDescending { invoiceGroup -> invoiceGroup.date }
    return invoicesGroup
}