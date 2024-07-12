package com.casecode.pos.feature.stepper.employees

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.feature.stepper.R
import com.casecode.pos.core.ui.R.string as uiString
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.ui.EmployeeEmptyScreen
import com.casecode.pos.core.model.data.users.Employee

import com.casecode.pos.feature.stepper.StepperBusinessUiState
import com.casecode.pos.feature.stepper.StepperBusinessViewModel

@Composable
fun EmployeesStepperScreen(viewModel: StepperBusinessViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showEmployeeDialog by rememberSaveable  { mutableStateOf(false) }
    var showUpdateEmployeeDialog by rememberSaveable { mutableStateOf(false) }

    EmployeesStepperScreen(
        uiState,
        onAddClick = { showEmployeeDialog = true },
        onEmployeeClick = {
            viewModel.setEmployeeSelected(it)
            showUpdateEmployeeDialog = true
        },
        onDoneClick = viewModel::checkNetworkThenSetEmployees,
        onPreviousClick = viewModel::previousStep,
    )
    if (showEmployeeDialog) {
        EmployeeStepperDialog(onDismiss = { showEmployeeDialog = false }, viewModel = viewModel)
    }
    if (showUpdateEmployeeDialog) {
        EmployeeStepperDialog(
            onDismiss = { showUpdateEmployeeDialog = false },
            isUpdate = true, viewModel = viewModel,
        )
    }
}

@Composable
fun EmployeesStepperScreen(
    uiState: StepperBusinessUiState,
    onAddClick: () -> Unit,
    onEmployeeClick: (Employee) -> Unit,
    onDoneClick: () -> Unit, onPreviousClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            EmployeesHeader(onAddClick = onAddClick)
            if (uiState.employees.isEmpty()) {
                EmployeeEmptyScreen()
            } else {
                EmployeesList(employees = uiState.employees, onEmployeeClick = onEmployeeClick)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            PosTextButton(
                onClick = onPreviousClick,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
                        contentDescription = null,
                    )
                },
                text = { Text(stringResource(id = R.string.feature_stepper_previous_button_text)) },
            )
            PosTextButton(
                onClick = onDoneClick,
                modifier = Modifier
                    .wrapContentSize(),
            ) {
                Text(stringResource(id = R.string.feature_stepper_done_button_text))
            }

        }
    }
}



@Composable
fun EmployeesHeader(onAddClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.SupervisedUserCircle,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(24.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = uiString.core_ui_employee_header_title),
        )
        Spacer(modifier = Modifier.weight(1f)) // Push the button to the end
        IconButton(onClick = onAddClick) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(uiString.core_ui_add_employee_title),
            )
        }
    }
}


@com.casecode.pos.core.ui.DevicePreviews
@Composable
private fun EmployeesScreenEmptyPreview() {
    POSTheme {
        PosBackground {
            EmployeesStepperScreen(
                uiState = StepperBusinessUiState(),
                onAddClick = {},
                onEmployeeClick = {},
                onDoneClick = {},
                onPreviousClick = {},
            )
        }
    }
}

@com.casecode.pos.core.ui.DevicePreviews
@Composable
private fun EmployeesScreenPreview() {
    POSTheme {
        PosBackground {
            EmployeesStepperScreen(
                uiState = StepperBusinessUiState(
                    employees = mutableListOf(
                        Employee(
                            "name",
                            "phone",
                            "password",
                            "branchName",
                            "admin",
                        ),
                        Employee(
                            "name1",
                            "phone",
                            "password",
                            "branchName",
                            "sale",
                        ),
                        Employee(
                            "name1",
                            "phone",
                            "password",
                            "branchName",
                            "sale",
                        ),
                        Employee(
                            "name1",
                            "phone",
                            "password",
                            "branchName",
                            "sale",
                        ),
                        Employee(
                            "name1",
                            "phone",
                            "password",
                            "branchName",
                            "sale",
                        ),
                        Employee(
                            "name1",
                            "phone",
                            "password",
                            "branchName",
                            "sale",
                        ),
                        Employee(
                            "name1",
                            "phone",
                            "password",
                            "branchName",
                            "sale",
                        ),
                        Employee(
                            "name1",
                            "phone",
                            "password",
                            "branchName",
                            "sale",
                        ),
                    ),
                ),
                onAddClick = {}, onEmployeeClick = {}, onDoneClick = {}, onPreviousClick = {},
            )
        }
    }
}