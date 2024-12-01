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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.data.utils.toFormattedDateString
import com.casecode.pos.core.designsystem.component.PosBackground
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        if (paymentDetails.isEmpty()) {
            BillPaymentEmpty(Modifier.align(Alignment.CenterHorizontally))
        } else {
            paymentDetails.forEach { payment ->
                BillPaymentItem(paymentDetails = payment)
            }
        }
    }
}

@Composable
private fun BillPaymentEmpty(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(
                id = com.casecode.pos.core.ui.R.drawable.core_ui_ic_outline_inventory_120,
            ),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
        )

        Text(
            text = stringResource(id = R.string.feature_bill_payment_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            modifier = Modifier.padding(top = 16.dp),
        )

        Text(
            text = stringResource(id = R.string.feature_bill_payment_empty_message),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun BillPaymentItem(paymentDetails: PaymentDetails) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.feature_bill_tap_payment_text) + paymentDetails.paymentId,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        supportingContent = {
            Text(
                text = paymentDetails.paymentDate.toFormattedDateString(),
                style = MaterialTheme.typography.labelSmall,
            )
        },
        trailingContent = {
            Column {
                Text(
                    text = paymentDetails.amountPaid.toBigDecimalFormatted(),
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(text = stringResource(toPaymentMethodRes(paymentDetails.paymentMethod)))
            }
        },
    )
}

@DevicePreviews
@Composable
fun BillPaymentTapPreview(
    @PreviewParameter(SupplierInvoiceParameterProvider::class) invoiceSupplier: List<SupplierInvoice>,
) {
    POSTheme {
        BillPaymentTap(paymentDetails = invoiceSupplier[1].paymentDetails)
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
        paymentMethod = PaymentMethod.CASH,
        amountPaid = 123.45,
    )
    BillPaymentItem(paymentDetails)
}