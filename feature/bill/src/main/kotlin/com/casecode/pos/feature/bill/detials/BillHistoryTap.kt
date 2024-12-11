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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.data.utils.toFormattedDateTimeString
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.PaymentDetails
import com.casecode.pos.core.model.data.users.PaymentMethod
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.ui.parameterprovider.SupplierInvoiceParameterProvider
import com.casecode.pos.feature.bill.R
import kotlinx.datetime.Clock.System
import kotlinx.datetime.Instant

@Composable
fun BillHistoryTap(
    modifier: Modifier = Modifier,
    total: Double,
    issueDate: Instant,
    createdBy: String,
    paymentDetails: List<PaymentDetails>,
) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        BillHistoryFirstLine(issueDate, createdBy, total)
        paymentDetails.forEachIndexed { index, paymentDetail ->
            BillHistoryLine(isLastStep = index == paymentDetails.lastIndex, paymentDetail)
        }
    }
}

@Composable
private fun BillHistoryFirstLine(issueDate: Instant, createdBy: String, subTotal: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Rounded.Circle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(16.dp),
            )
            VerticalDivider(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight(),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(Modifier.padding(bottom = 16.dp)) {
            Text(text = stringResource(R.string.feature_bill_history_created_for_text, subTotal))
            Text(
                text = createdBy,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = issueDate.toFormattedDateTimeString(),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun BillHistoryLine(isLastStep: Boolean, paymentDetails: PaymentDetails) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight(),
        ) {
            Icon(
                imageVector = Icons.Rounded.Circle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(16.dp),
            )
            if (!isLastStep) {
                VerticalDivider(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight(),
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(Modifier.padding(bottom = 8.dp)) {
            Text(
                text = stringResource(
                    R.string.feature_bill_history_payment_of_made_text,
                    paymentDetails.amountPaid,
                ),
            )
            Text(
                text = paymentDetails.createdBy,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = paymentDetails.paymentDate.toFormattedDateTimeString(),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview
@Composable
fun BillHistoryTapPreview(
    @PreviewParameter(SupplierInvoiceParameterProvider::class) bills: List<SupplierInvoice>,
) {
    POSTheme {
        PosBackground {
            BillHistoryTap(
                modifier = Modifier,
                total = bills[1].subTotal,
                issueDate = bills[1].issueDate,
                createdBy = bills[1].createdBy,
                paymentDetails = bills[1].paymentDetails,
            )
        }
    }
}

@Preview
@Composable
private fun BillHistoryFirstLinePreview() {
    POSTheme {
        PosBackground {
            BillHistoryFirstLine(
                issueDate = System.now(),
                createdBy = "John Doe",
                subTotal = 123.45,
            )
        }
    }
}

@Preview
@Composable
private fun BillHistoryLinePreview() {
    val paymentDetails = PaymentDetails(
        paymentId = "paymentId",
        paymentDate = System.now(),
        createdBy = "John Doe",
        paymentMethod = PaymentMethod.CASH,
        amountPaid = 100.0,
    )
    POSTheme {
        PosBackground {
            BillHistoryLine(isLastStep = false, paymentDetails = paymentDetails)
        }
    }
}