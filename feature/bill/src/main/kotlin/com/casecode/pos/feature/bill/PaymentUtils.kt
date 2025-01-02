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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.casecode.pos.core.model.data.users.PaymentMethod
import com.casecode.pos.core.model.data.users.PaymentStatus

@StringRes
internal fun toPaymentRes(status: PaymentStatus): Int = when (status) {
    PaymentStatus.PENDING -> R.string.feature_bill_payment_status_pending
    PaymentStatus.PAID -> R.string.feature_bill_payment_status_paid
    PaymentStatus.PARTIALLY_PAID -> R.string.feature_bill_payment_status_partially_paid
    PaymentStatus.OVERDUE -> R.string.feature_bill_payment_status_overdue
}

@StringRes
internal fun toPaymentMethodRes(method: PaymentMethod): Int = when (method) {
    PaymentMethod.CASH -> R.string.feature_bill_payment_method_cash
    PaymentMethod.CREDIT_CARD -> R.string.feature_bill_payment_method_card
    PaymentMethod.DIGITAL_PAYMENT -> R.string.feature_bill_payment_method_digital
}

internal fun fromPaymentMethodRes(@StringRes resourceId: Int): PaymentMethod = when (resourceId) {
    R.string.feature_bill_payment_method_cash -> PaymentMethod.CASH
    R.string.feature_bill_payment_method_card -> PaymentMethod.CREDIT_CARD
    R.string.feature_bill_payment_method_digital -> PaymentMethod.DIGITAL_PAYMENT
    else -> throw IllegalArgumentException("Unknown payment method resource ID: $resourceId")
}

@Composable
internal fun toPaymentStatusColor(status: PaymentStatus): Color {
    val paymentStatusTextColor = when (status) {
        PaymentStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer
        PaymentStatus.PAID -> MaterialTheme.colorScheme.primaryContainer
        PaymentStatus.PARTIALLY_PAID -> MaterialTheme.colorScheme.tertiaryContainer
        PaymentStatus.OVERDUE -> MaterialTheme.colorScheme.errorContainer
    }
    return paymentStatusTextColor
}