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
package com.casecode.pos.feature.setting.printer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.PrinterConnectionInfo
import com.casecode.pos.core.model.data.PrinterInfo
import com.casecode.pos.feature.setting.R

@Composable
internal fun PrinterScreen(
    printerVIewModel: PrinterViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onPrinterInfoClick: () -> Unit,
) {
    val uiState = printerVIewModel.printersUiState.collectAsStateWithLifecycle()
    PrinterScreen(
        resourcePrinters = uiState.value,
        onBackClick = onBackClick,
        onAddClick = onPrinterInfoClick,
        onPrinterItemClick = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterScreen(
    modifier: Modifier = Modifier,
    resourcePrinters: Resource<List<PrinterInfo>>,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onPrinterItemClick: (PrinterInfo) -> Unit,
) {
    Column(
        modifier =
        modifier
            .fillMaxSize(),
    ) {
        PosTopAppBar(
            modifier = Modifier,
            titleRes = R.string.feature_settings_printer_title,
            navigationIcon = PosIcons.ArrowBack,
            navigationIconContentDescription = null,
            colors =
            TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            onNavigationClick = onBackClick,
        )
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            when (resourcePrinters) {
                is Resource.Loading -> {
                    PosLoadingWheel(
                        modifier =
                        modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        contentDesc = "LoadingPrintersInfo",
                    )
                }

                is Resource.Empty -> {
                    PrintersEmptyScreen()
                }

                is Resource.Error -> PrintersEmptyScreen()
                is Resource.Success -> {
                    PrinterList(
                        printers = resourcePrinters.data,
                        onPrinterClick = onPrinterItemClick,
                    )
                }
            }

            FloatingActionButton(
                onClick = { onAddClick() },
                modifier = Modifier.align(Alignment.BottomEnd),
            ) {
                Icon(
                    imageVector = PosIcons.Add,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
fun PrintersEmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(
                id = com.casecode.pos.core.ui.R.drawable.core_ui_ic_outline_inventory_120,
            ),
            contentDescription = stringResource(id = R.string.feature_setting_sign_out_button_text),
            modifier = Modifier.size(120.dp),
        )

        Text(
            text = stringResource(id = R.string.feature_setting_sign_out_button_text),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            modifier = Modifier.padding(top = 16.dp),
        )

        Text(
            text = stringResource(id = R.string.feature_setting_sign_out_button_text),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
fun PrinterList(
    printers: List<PrinterInfo>,
    onPrinterClick: (PrinterInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        val scrollableState = rememberLazyListState()

        LazyColumn(
            modifier = modifier.padding(horizontal = 8.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            state = scrollableState,
        ) {
            printers.forEach { printer ->
                val printerName = printer.name
                item(key = printerName) {
                    PrinterItem(
                        name = printerName,
                        printerWidth = printer.widthPaper,
                        printerType = "printer.connectionType.toString()",
                        onClick = { onPrinterClick(printer) },
                    )
                }
            }
        }
    }
}

@Composable
fun PrinterItem(
    modifier: Modifier = Modifier,
    name: String,
    printerType: String,
    printerWidth: String,
    onClick: () -> Unit,
) {
    ElevatedCard(modifier.padding(bottom = 8.dp)) {
        ListItem(
            headlineContent = { Text(name) },
            supportingContent = {
                Column(modifier = Modifier.height(IntrinsicSize.Min)) {
                    Text(printerWidth)
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                    Text(printerType)
                }
            },
            modifier =
            modifier.clickable {
                onClick()
            },
        )
    }
}

@Preview
@Composable
fun PrinterScreenSuccessPreview() {
    POSTheme {
        PosBackground {
            PrinterScreen(
                resourcePrinters =
                Resource.Success(
                    listOf(
                        PrinterInfo(
                            name = "Orlando Reed",
                            connectionTypeInfo = PrinterConnectionInfo.Tcp("", 123),
                            isDefaultPrint = false,
                            widthPaper = "tamquam",
                        ),
                        PrinterInfo(
                            name = "Stefan Cobb",
                            connectionTypeInfo = PrinterConnectionInfo.Tcp("", 123),
                            isDefaultPrint = false,
                            widthPaper = "nostra",
                        ),
                    ),
                ),
                onBackClick = {},
                onAddClick = {},
                onPrinterItemClick = { },
            )
        }
    }
}