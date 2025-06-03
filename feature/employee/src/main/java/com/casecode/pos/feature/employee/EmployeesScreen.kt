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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Employee
import com.casecode.pos.core.ui.DeleteDialog
import com.casecode.pos.core.ui.EmployeeEmptyScreen
import com.casecode.pos.core.ui.R.string as uiString

@Composable
fun EmployeesScreen(viewModel: EmployeeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showEmployeeDialog by remember { mutableStateOf(false) }
    var showUpdateEmployeeDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUserAdminQrDialog by remember { mutableStateOf(false) }

    EmployeesScreen(
        uiState,
        onActionClick = { showUserAdminQrDialog = true },
        onAddClick = { showEmployeeDialog = true },
        onEmployeeClick = {
            showUpdateEmployeeDialog = true
            viewModel.setEmployeeSelected(it)
        },
        onItemLongClick = {
            viewModel.setEmployeeSelected(it)
            showDeleteDialog = true
        },
        onMessageShown = viewModel::snackbarMessageShown,
    )
    if (showUserAdminQrDialog) {
        UserAdminQrDialog(onDismiss = { showUserAdminQrDialog = false })
    }
    if (showEmployeeDialog) {
        EmployeeDialog(onDismiss = { showEmployeeDialog = false }, viewModel = viewModel)
    }
    if (showUpdateEmployeeDialog) {
        EmployeeDialog(
            onDismiss = { showUpdateEmployeeDialog = false },
            isUpdate = true,
            viewModel = viewModel,
        )
    }
    if (showDeleteDialog) {
        DeleteDialog(
            titleRes = R.string.feature_employee_dialog_delete_title,
            messageRes = R.string.feature_employee_dialog_delete_message,
            onConfirm = {
                viewModel.deleteEmployee()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesScreen(
    uiState: UiEmployeesState,
    modifier: Modifier = Modifier,
    onActionClick: () -> Unit = {},
    onAddClick: () -> Unit,
    onEmployeeClick: (Employee) -> Unit = {},
    onItemLongClick: (Employee) -> Unit = {},
    onMessageShown: () -> Unit = {},
) {
    val snackState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddClick() }, modifier = Modifier.padding(16.dp)) {
                Icon(
                    imageVector = PosIcons.Add,
                    contentDescription = stringResource(uiString.core_ui_add_employee_button_text),
                )
            }
        },
    ) { padding ->
        Column(modifier = modifier.fillMaxSize().padding(padding)) {
            PosTopAppBar(
                modifier = modifier,
                titleRes = uiString.core_ui_employee_header_title,
                onActionClick = { onActionClick() },
                actionIconContentDescription = null,
                actionIcon = PosIcons.UserAdman,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
            when (uiState.resourceEmployees) {
                is Resource.Empty -> {
                    EmployeeEmptyScreen()
                }

                is Resource.Error -> {
                    EmployeeEmptyScreen()
                }

                Resource.Loading -> {
                    PosLoadingWheel(
                        modifier = modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                        contentDesc = "LoadingEmployees",
                    )
                }

                is Resource.Success -> {
                    EmployeesContent(
                        uiState.resourceEmployees.data,
                        onEmployeeClick = onEmployeeClick,
                        onEmployeeLongClick = onItemLongClick,
                    )
                }
            }
        }
    }
    uiState.userMessage?.let { message ->
        val snackbarText = stringResource(message)
        LaunchedEffect(message) {
            snackState.showSnackbar(snackbarText)
            onMessageShown()
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
        items(employees) { employee ->
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
            overlineContent = { Text(text = employee.name) },
            headlineContent = { Text(employee.permission + " / " + employee.branchName) },
            supportingContent = { Text(text = employee.phoneNumber) },
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
                    ),
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
}