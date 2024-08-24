package com.casecode.pos.feature.employee

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Employee
import com.casecode.pos.core.ui.EmployeeEmptyScreen
import com.casecode.pos.core.ui.R.string as uiString

@Composable
fun EmployeesRoute(viewModel: EmployeeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showEmployeeDialog by remember { mutableStateOf(false) }
    var showUpdateEmployeeDialog by remember { mutableStateOf(false) }
    val snackState = remember { SnackbarHostState() }

    SnackbarHost(
        hostState = snackState,
        Modifier
            .padding(8.dp)
            .zIndex(1f),
    )
    uiState.userMessage?.let { message ->
        val snackbarText = stringResource(message)
        LaunchedEffect(snackState, uiState, message, snackbarText) {
            snackState.showSnackbar(snackbarText)
            viewModel.snackbarMessageShown()
        }
    }
    EmployeesScreen(
        uiState,
        onAddClick = { showEmployeeDialog = true },
        onEmployeeClick = {
            showUpdateEmployeeDialog = true
            viewModel.setEmployeeSelected(it)
        },
        onItemLongClick = {},
    )
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
}

@Composable
fun EmployeesScreen(
    uiState: UiEmployeesState,
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit,
    onEmployeeClick: (Employee) -> Unit = {},
    onItemLongClick: (Employee) -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        FloatingActionButton(
            onClick = {
                onAddClick()
            },
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .zIndex(1f),
        ) {
            Icon(
                imageVector = PosIcons.Add,
                contentDescription = stringResource(uiString.core_ui_add_employee_button_text),
            )
        }

        when (uiState.resourceEmployees) {
            is Resource.Empty -> {
                EmployeeEmptyScreen()
            }

            is Resource.Error -> {
                EmployeeEmptyScreen()
            }

            Resource.Loading -> {
                PosLoadingWheel(
                    modifier =
                    modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    contentDesc = "LoadingItems",
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

@Composable
private fun EmployeesContent(
    employees: List<Employee>,
    onEmployeeClick: (Employee) -> Unit,
    onEmployeeLongClick: (Employee) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 8.dp),
    ) {
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
            overlineContent = {
                Text(
                    text = employee.name,
                )
            },
            headlineContent = { Text(employee.permission + " / " + employee.branchName) },
            supportingContent = {
                Text(
                    text = employee.phoneNumber,
                )
            },
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
                .combinedClickable(
                    onClick = onItemClick,
                    onLongClick = onItemLongClick,
                ),
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
            uiState = UiEmployeesState(resourceEmployees = Resource.error(uiString.core_ui_error_unknown)),
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