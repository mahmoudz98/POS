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
package com.casecode.pos.feature.employee

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.ui.EmployeeEmptyScreen
import com.casecode.pos.core.ui.PosDeleteDialog
import com.casecode.pos.core.ui.TrackScreenViewEvent
import com.casecode.pos.core.ui.TrackScrollJank
import com.casecode.pos.core.ui.business.toDisplayString
import com.casecode.pos.core.ui.parameterprovider.EmployeesPreviewParameterProvider
import com.casecode.pos.core.ui.R.string as uiString

@Composable
internal fun EmployeesScreen(
    viewModel: EmployeeViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String) -> Boolean,
) {
    TrackScreenViewEvent(screenName = "EmployeesScreen")
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EmployeesScreen(
        uiState = uiState,
        onEventClick = {
            if (it is EmployeeEvent.NavigationBack) {
                onBackClick()
            } else {
                viewModel.onEvent(it)
            }
        },
    )

    when (uiState.dialogState) {
        DialogState.None -> Unit
        DialogState.CompanyCode -> {
            CompanyCodeQrDialog(onDismiss = { viewModel.onEvent(EmployeeEvent.CompanyCodeClosed) })
        }

        DialogState.Creating -> EmployeeFormDialog(
            onDismiss = {
                viewModel.onEvent(EmployeeEvent.EmployeeFormClosed)
            },
            viewModel = viewModel,
        )

        DialogState.Updating -> EmployeeFormDialog(
            onDismiss = {
                viewModel.onEvent(EmployeeEvent.EmployeeFormClosed)
            },
            isUpdate = true,
            viewModel = viewModel,
        )

        DialogState.Deleting -> PosDeleteDialog(
            titleRes = R.string.feature_employee_dialog_delete_title,
            messageRes = R.string.feature_employee_dialog_delete_message,
            onConfirm = {
                viewModel.deleteEmployee()
            },
            onDismiss = {
                viewModel.onEvent(EmployeeEvent.DeletingEmployeeClosed)
            },
        )
    }
    uiState.userMessage?.let {
        val snackbarText = stringResource(it)
        LaunchedEffect(Unit) {
            onShowSnackbar(snackbarText)
            viewModel.onEvent(EmployeeEvent.UserMessageShown)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesScreen(
    uiState: EmployeeUiState,
    modifier: Modifier = Modifier,
    onEventClick: (EmployeeEvent) -> Unit,

) {
    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            PosTopAppBar(
                modifier = modifier,
                titleRes = uiString.core_ui_employee_header_title,
                navigationIcon = PosIcons.ArrowBack,
                onNavigationClick = { onEventClick(EmployeeEvent.NavigationBack) },
                onActionClick = { onEventClick(EmployeeEvent.CompanyCodeOpened) },
                actionIconContentDescription = stringResource(R.string.feature_employee_dialog_title_company_code),
                actionIcon = PosIcons.UserAdman,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEventClick(EmployeeEvent.CreationEmployeeOpened) },
                modifier = Modifier.padding(16.dp),
            ) {
                Icon(
                    imageVector = PosIcons.Add,
                    contentDescription = stringResource(uiString.core_ui_add_employee_button_text),
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (uiState.employees.isEmpty() && !uiState.isLoading) {
                EmployeeEmptyScreen()
            } else {
                EmployeesContent(
                    employees = uiState.employees,
                    onEmployeeClick = { onEventClick(EmployeeEvent.UpdatingEmployeeOpened(it)) },
                    onEmployeeLongClick = { onEventClick(EmployeeEvent.DeletingEmployeeOpened(it)) },
                )
            }
            if (uiState.isLoading) {
                PosLoadingWheel(
                    modifier = modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    contentDesc = "LoadingEmployees",
                )
            }
        }
    }
}

@Composable
fun EmployeesContent(
    employees: List<Employee>,
    onEmployeeClick: (Employee) -> Unit,
    onEmployeeLongClick: (Employee) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    TrackScrollJank(scrollableState = lazyListState, stateName = "employee:list")
    LazyColumn(
        modifier = Modifier.padding(horizontal = 8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        state = lazyListState,
    ) {
        items(
            items = employees,
            key = { it.id },
        ) { employee ->
            EmployeeItem(
                employee = employee,
                onItemClick = { onEmployeeClick(employee) },
                onItemLongClick = { onEmployeeLongClick(employee) },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmployeeItem(
    employee: Employee,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
    onItemLongClick: () -> Unit = {},
) {
    ElevatedCard(modifier.padding(bottom = 8.dp)) {
        ListItem(
            leadingContent = {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(40.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = employee.name.take(1),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            },
            overlineContent = { Text(text = "#${employee.id}") },
            headlineContent = { Text(text = employee.name) },
            trailingContent = {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = employee.role.toDisplayString(),
                )
            },
            supportingContent = { Text(text = employee.phone) },
            colors =
            ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                headlineColor = MaterialTheme.colorScheme.onSurfaceVariant,
                overlineColor = MaterialTheme.colorScheme.onSurfaceVariant,
                supportingColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            modifier =
            modifier
                .fillMaxWidth()
                .combinedClickable(onClick = onItemClick, onLongClick = onItemLongClick),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeesScreenLoadingPreview() {
    POSTheme {
        EmployeesScreen(
            uiState = EmployeeUiState(isLoading = true),
            onEventClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeesScreenEmptyPreview() {
    POSTheme {
        EmployeesScreen(
            uiState = EmployeeUiState(),
            onEventClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeesScreenErrorPreview() {
    POSTheme {
        EmployeesScreen(
            uiState =
            EmployeeUiState(userMessage = uiString.core_ui_error_unknown),
            onEventClick = {},

        )
    }
}

@Preview
@Composable
fun EmployeesScreenSuccessPreview(
    @PreviewParameter(EmployeesPreviewParameterProvider::class) employees: List<Employee>,
) {
    POSTheme {
        PosBackground {
            EmployeesScreen(
                uiState = EmployeeUiState(employees = employees),
                onEventClick = {},
            )
        }
    }
}

@Preview
@Composable
fun EmployeesScreenSuccessWithLoadingPreview(
    @PreviewParameter(EmployeesPreviewParameterProvider::class) employees: List<Employee>,
) {
    POSTheme {
        PosBackground {
            EmployeesScreen(
                uiState = EmployeeUiState(
                    employees = employees,
                    isLoading = true,
                ),
                onEventClick = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeeItemPreview(
    @PreviewParameter(EmployeesPreviewParameterProvider::class) employees: List<Employee>,
) {
    POSTheme {
        EmployeeItem(employee = employees[0])
    }
}
