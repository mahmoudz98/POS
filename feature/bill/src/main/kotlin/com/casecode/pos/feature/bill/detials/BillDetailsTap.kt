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

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.data.utils.toFormattedDateString
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.parameterprovider.SupplierInvoiceParameterProvider
import com.casecode.pos.core.ui.utils.toBigDecimalFormatted
import com.casecode.pos.core.ui.utils.toFormattedString
import com.casecode.pos.feature.bill.R

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun BillDetailsTap(modifier: Modifier = Modifier, invoiceSupplier: SupplierInvoice) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.feature_bill_issue_date_hint),
                modifier = Modifier.weight(1f),
            )
            Text(
                text = invoiceSupplier.issueDate.toFormattedDateString(),
                modifier = Modifier.weight(2f),
            )
        }
        Row(
            Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.feature_bill_due_date_hint),
                modifier = Modifier.weight(1f),
            )
            Text(
                text = invoiceSupplier.dueDate.toFormattedDateString(),
                modifier = Modifier.weight(2f),
            )
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(R.string.feature_bill_items_text))
            Text(stringResource(R.string.feature_bill_items_amount_text))
        }
        HorizontalDivider()
        invoiceSupplier.invoiceItems.forEach {
            BillItem(
                name = it.name,
                quantity = it.quantity,
                costPrice = it.costPrice,
                sku = it.sku,
            )
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            val quarterWidth = maxWidth / 4
            Column(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.feature_bill_subtotal_text),
                        modifier = Modifier.padding(start = quarterWidth),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = invoiceSupplier.subTotal.toFormattedString(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                if (invoiceSupplier.amountDiscounted > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.feature_bill_discount_text),
                            modifier = Modifier.padding(start = quarterWidth),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = stringResource(R.string.feature_bill_minus_text) +
                                invoiceSupplier.totalAmount.minus(invoiceSupplier.subTotal)
                                    .toFormattedString(),
                            color = MaterialTheme.colorScheme.errorContainer,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.feature_bill_total_text),
                        modifier = Modifier.padding(start = quarterWidth),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = invoiceSupplier.totalAmount.toBigDecimalFormatted(),
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.feature_bill_payment_made_text),
                        modifier = Modifier.padding(start = quarterWidth),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = stringResource(R.string.feature_bill_minus_text) +
                            invoiceSupplier.paymentDetails.sumOf { it.amountPaid }
                                .toBigDecimalFormatted(),
                        color = MaterialTheme.colorScheme.errorContainer,

                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.feature_bill_rest_due_text),
                        modifier = Modifier.padding(start = quarterWidth),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = invoiceSupplier.totalAmount.minus(
                            invoiceSupplier.paymentDetails.sumOf { it.amountPaid },
                        ).toBigDecimalFormatted(),
                    )
                }
            }
        }
    }
}

@Composable
fun BillItem(
    modifier: Modifier = Modifier,
    name: String,
    quantity: Int,
    costPrice: Double,
    sku: String,
) {
    ListItem(
        leadingContent = {
        },
        headlineContent = {
            Text(name)
        },
        trailingContent = {
            Text(quantity.times(costPrice).toBigDecimalFormatted())
        },
        supportingContent = {
            Column {
                Text("$quantity x ${costPrice.toBigDecimal()}")
                Text("SKU: $sku")
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = modifier.fillMaxWidth(),
    )
}

@DevicePreviews
@Composable
fun BillDetailsTapPreview(
    @PreviewParameter(SupplierInvoiceParameterProvider::class) invoiceSupplier: List<SupplierInvoice>,
) {
    POSTheme {
        PosBackground {
            LazyColumn {
                item {
                    BillDetailsTap(invoiceSupplier = invoiceSupplier[0])
                }
            }
        }
    }
}