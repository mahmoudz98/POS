package com.casecode.pos.feature.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.feature.reports.R

@Composable
fun ReportsScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.fetchInvoices()
    }
    ReportsScreen(uiState)

}
@Composable
internal fun ReportsScreen(uiState: UiReportsState, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        ReportsHeaderContent(totalSalesToday = uiState.totalInvoiceSalesToday, countInvoiceToday = uiState.countOfInvoice){
            // TODO: Handle print reports today.
        }

    }
}
@Composable
private fun ReportsHeaderContent(totalSalesToday: Double, countInvoiceToday:Int, onPrintClick:()->Unit){
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(text = stringResource(R.string.feature_reports_today_label))
            Text(modifier = Modifier.padding(start = 4.dp), text = stringResource(R.string.feature_reports_sales_label) + totalSalesToday)
            Text(modifier = Modifier.padding(start = 4.dp), text = stringResource(R.string.feature_reports_count_invoices_today_label) + countInvoiceToday)
        }
        IconButton(onClick = onPrintClick) {
            Icon(imageVector = PosIcons.Print, contentDescription = null)
        }
    }
    HorizontalDivider(Modifier.padding( top = 4.dp, start = 8.dp, end = 8.dp))

}

@Preview
@Composable
fun ReportsScreenPreview() {
    POSTheme {  PosBackground {
        ReportsScreen(UiReportsState())

    }}
}