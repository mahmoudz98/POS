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
package com.casecode.pos.feature.bill.detials

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.data.utils.toFormattedDateString
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosEmptyScreen
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.PaymentDetails
import com.casecode.pos.core.model.data.users.PaymentMethod
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.parameterprovider.SupplierInvoiceParameterProvider
import com.casecode.pos.core.ui.utils.toBigDecimalFormatted
import com.casecode.pos.feature.bill.R
import com.casecode.pos.feature.bill.toPaymentMethodRes
import kotlinx.datetime.Clock

@Composable
fun BillPaymentTap(modifier: Modifier = Modifier, paymentDetails: List<PaymentDetails>) {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 8.dp)) {
        if (paymentDetails.isEmpty()) {
            PosEmptyScreen(
                icon = PosIcons.Payment,
                titleRes = R.string.feature_bill_payment_empty_title,
                messageRes = R.string.feature_bill_payment_empty_message,
            )
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(max = Short.MAX_VALUE.toInt().dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(paymentDetails) { payment ->
                    BillPaymentItem(
                        modifier.fillMaxWidth(),
                        paymentDetails = payment,
                    )
                }
            }
        }
    }
}

@Composable
private fun BillPaymentItem(modifier: Modifier = Modifier, paymentDetails: PaymentDetails) {
    Row(
        modifier = modifier,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.feature_bill_tap_payment_text) +
                    paymentDetails.paymentId,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = paymentDetails.paymentDate.toFormattedDateString(),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
            )
        }
        Column(
            Modifier.width(IntrinsicSize.Max),
        ) {
            Text(
                text = paymentDetails.amountPaid.toBigDecimalFormatted(),
                style = MaterialTheme.typography.labelSmall,
            )
            Text(text = stringResource(toPaymentMethodRes(paymentDetails.paymentMethod)))
        }
    }
}

@DevicePreviews
@Composable
fun BillPaymentTapPreview(
    @PreviewParameter(SupplierInvoiceParameterProvider::class)
    invoiceSupplier: List<SupplierInvoice>,
) {
    POSTheme {
        PosBackground {
            BillPaymentTap(paymentDetails = invoiceSupplier[1].paymentDetails)
        }
    }
}

@Preview
@Composable
fun BillPaymentTapEmptyPreview() {
    POSTheme {
        PosBackground {
            BillPaymentTap(paymentDetails = emptyList())
        }
    }
}

@Preview
@Composable
fun BillPaymentItemPreview() {
    val paymentDetails = PaymentDetails(
        paymentId = "1234567890",
        paymentDate = Clock.System.now(),
        createdBy = "John Doe",
        paymentMethod = PaymentMethod.DIGITAL_PAYMENT,
        amountPaid = 123.45,
    )
    POSTheme {
        PosBackground {
            BillPaymentItem(paymentDetails = paymentDetails)
        }
    }
}