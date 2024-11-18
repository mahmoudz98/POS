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

import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_PAYMENT_DETAILS_AMOUNT_PAID_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_PAYMENT_DETAILS_CREATED_BY_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_PAYMENT_DETAILS_DATE_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_PAYMENT_DETAILS_ID_FIELD
import com.casecode.pos.core.firebase.services.SUPPLIER_INVOICE_PAYMENT_DETAILS_METHOD_FIELD
import com.casecode.pos.core.model.data.users.PaymentDetails

internal fun PaymentDetails.asExternalMapper(): Map<String, Comparable<*>> = mapOf(
    SUPPLIER_INVOICE_PAYMENT_DETAILS_ID_FIELD to this.paymentId,
    SUPPLIER_INVOICE_PAYMENT_DETAILS_DATE_FIELD to this.paymentDate,
    SUPPLIER_INVOICE_PAYMENT_DETAILS_CREATED_BY_FIELD to this.createdBy,
    SUPPLIER_INVOICE_PAYMENT_DETAILS_METHOD_FIELD to this.paymentMethod.name,
    SUPPLIER_INVOICE_PAYMENT_DETAILS_AMOUNT_PAID_FIELD to this.amountPaid,
)