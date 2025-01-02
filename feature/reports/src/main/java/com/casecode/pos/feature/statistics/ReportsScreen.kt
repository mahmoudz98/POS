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
package com.casecode.pos.feature.statistics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.component.shimmer
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.feature.reports.R

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel(),
    onSalesReportClick: () -> Unit = {},
    onInventoryReportClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.fetchInvoices()
    }

    ReportsScreen(
        uiState,
        onSalesReportClick = onSalesReportClick,
        onInventoryReportClick = onInventoryReportClick,
    )
}

@Composable
internal fun ReportsScreen(
    uiState: UiReportsState,
    modifier: Modifier = Modifier,
    onSalesReportClick: () -> Unit = {},
    onInventoryReportClick: () -> Unit = {},
) {
    Column(
        modifier = modifier.padding(16.dp),
    ) {
        PosTextButton(
            onClick = onSalesReportClick,
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.feature_reports_sales_button_text),
            leadingIcon = PosIcons.Reports,
        )
        PosTextButton(
            onClick = onInventoryReportClick,
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.feature_reports_inventory_button_text),
            leadingIcon = PosIcons.Invoices,

        )
        HorizontalDivider(Modifier.padding(top = 12.dp, end = 8.dp))

        ReportsSalesTodaySection(
            isLoading = uiState.isLoading,
            isEmpty = uiState.isEmpty,
            totalSalesToday = uiState.totalInvoiceSalesToday,
            countInvoiceToday = uiState.countOfInvoice,
        )
    }
}

@Composable
private fun ReportsSalesTodaySection(
    isLoading: Boolean,
    isEmpty: Boolean,
    totalSalesToday: Double,
    countInvoiceToday: Int,
) {
    Column(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        when {
            isLoading -> {
                Box(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .shimmer(),
                )
            }

            isEmpty -> {
                Text(
                    text = stringResource(R.string.feature_reports_no_sales_today),
                    style = MaterialTheme.typography.titleSmall,
                )
            }

            else -> { // !isLoading && !isEmpty
                Column {
                    Text(
                        text = stringResource(R.string.feature_reports_today_label),
                        style = MaterialTheme.typography.titleSmall,
                    )

                    Text(
                        text =
                        stringResource(R.string.feature_reports_sales_label) +
                            stringResource(
                                com.casecode.pos.core.ui.R.string.core_ui_currency,
                                totalSalesToday,
                            ),
                        modifier = Modifier.padding(start = 4.dp),
                    )
                    Text(
                        text = stringResource(R.string.feature_reports_count_invoices_today_label) + countInvoiceToday,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
            }
        }
    }
}

@DevicePreviews
@Composable
fun ReportsScreenPreview() {
    POSTheme {
        PosBackground {
            ReportsScreen(UiReportsState(), onSalesReportClick = {}, onInventoryReportClick = {})
        }
    }
}

@Preview
@Composable
fun ReportsSalesTodayContentPreview() {
    POSTheme {
        PosBackground {
            ReportsSalesTodaySection(
                isLoading = true,
                isEmpty = false,
                totalSalesToday = 0.0,
                countInvoiceToday = 0,
            )
        }
    }
}