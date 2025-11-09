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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.ui.EmployeeEmptyScreen
import com.casecode.pos.core.ui.PosDeleteDialog
import com.casecode.pos.core.ui.business.toDisplayString
import com.casecode.pos.core.ui.R.string as uiString

@Composable
internal fun EmployeesScreen(
    viewModel: EmployeeViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String) -> Boolean,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showUserAdminQrDialog by remember { mutableStateOf(false) }

    EmployeesScreen(
        uiState = uiState,
        onActionClick = { showUserAdminQrDialog = true },
        onAddClick = {
            viewModel.onEvent(EmployeeEvent.CreationEmployeeOpened)
        },
        onEmployeeClick = {
            viewModel.onEvent(EmployeeEvent.UpdatingEmployeeOpened(it))
        },
        onItemLongClick = {
            viewModel.onEvent(EmployeeEvent.DeletingEmployeeOpened(it))
        },
    )
    if (showUserAdminQrDialog) {
        UserAdminQrDialog(onDismiss = { showUserAdminQrDialog = false })
    }
    when (uiState.dialogState) {
        DialogState.None -> Unit
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
    onActionClick: () -> Unit = {},
    onAddClick: () -> Unit,
    onEmployeeClick: (Employee) -> Unit,
    onItemLongClick: (Employee) -> Unit,
) {
    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddClick() }, modifier = Modifier.padding(16.dp)) {
                Icon(
                    imageVector = PosIcons.Add,
                    contentDescription = stringResource(uiString.core_ui_add_employee_button_text),
                )
            }
        },
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it),
        ) {
            PosTopAppBar(
                modifier = modifier,
                titleRes = uiString.core_ui_employee_header_title,
                onActionClick = { onActionClick() },
                actionIconContentDescription = null,
                actionIcon = PosIcons.UserAdman,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
            if (uiState.employees.isEmpty()) {
                EmployeeEmptyScreen()
            } else {
                EmployeesContent(
                    uiState.employees,
                    onEmployeeClick = onEmployeeClick,
                    onEmployeeLongClick = onItemLongClick,
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
private fun EmployeesContent(
    employees: List<Employee>,
    onEmployeeClick: (Employee) -> Unit,
    onEmployeeLongClick: (Employee) -> Unit,
) {
    LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
        items(employees) {
            EmployeeItem(
                employee = it,
                onItemClick = { onEmployeeClick(it) },
                onItemLongClick = { onEmployeeLongClick(it) },
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
            overlineContent = { Text(text = employee.name) },
            headlineContent = { Text(employee.role.toDisplayString()) },
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

/*@Preview(showBackground = true)
@Composable
fun EmployeesScreenLoadingPreview() {
    POSTheme {
        EmployeesScreen(
            uiState = UiEmployeesState(resourceEmployees = Resource.loading()),
            onAddClick = {},
            onEmployeeClick = {},
            onItemLongClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeesScreenEmptyPreview() {
    POSTheme {
        EmployeesScreen(
            uiState = UiEmployeesState(resourceEmployees = Resource.empty()),
            onAddClick = {},
            onEmployeeClick = {},
            onItemLongClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeesScreenErrorPreview() {
    POSTheme {
        EmployeesScreen(
            uiState =
                UiEmployeesState(resourceEmployees = Resource.error(uiString.core_ui_error_unknown)),
            onAddClick = {},
            onEmployeeClick = {},
            onItemLongClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeesScreenSuccessPreview() {
    POSTheme {
        EmployeesScreen(
            uiState =
                UiEmployeesState(
                    resourceEmployees =
                        Resource.Success(
                            listOf(
                                Employee(
                                    name = "Lillie Humphrey",
                                    phoneNumber = "(113) 581-4083",
                                    password = null,
                                    branchName = "branch2",
                                    permission = "sale",
                                ),
                                Employee(
                                    name = "Jeanine Moran",
                                    phoneNumber = "(799) 177-5393",
                                    password = null,
                                    branchName = "bransh 1",
                                    permission = "admin",
                                ),
                            )
                        ),
                ),
            onAddClick = {},
            onEmployeeClick = {},
            onItemLongClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeeItemPreview() {
    POSTheme {
        EmployeeItem(
            employee =
                Employee(
                    name = "John Doe",
                    phoneNumber = "123-456-7890",
                    permission = "Admin",
                    branchName = "Branch 1",
                    password = "password",
                ),
        )
    }
}*/
