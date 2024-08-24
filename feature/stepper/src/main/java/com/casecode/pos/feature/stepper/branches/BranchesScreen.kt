package com.casecode.pos.feature.stepper.branches

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.ui.BranchesHeader
import com.casecode.pos.feature.stepper.R
import com.casecode.pos.feature.stepper.StepperBusinessUiState
import com.casecode.pos.feature.stepper.StepperBusinessViewModel

@Composable
fun BranchesScreen(viewModel: StepperBusinessViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddBranchDialog by remember { mutableStateOf(false) }
    var showUpdateBranchDialog by remember { mutableStateOf(false) }
    BranchesScreen(
        uiState = uiState,
        onAddBranch = {
            showAddBranchDialog = true
        },
        onUpdateClick = {
            showUpdateBranchDialog = true
            viewModel.setBranchSelected(it)
        },
        onNextClick = viewModel::setBusiness,
        onPreviousClick = viewModel::previousStep,
    )
    if (showAddBranchDialog) {
        BranchDialog(
            viewModel = viewModel,
            onDismissRequest = { showAddBranchDialog = false },
        )
    }
    if (showUpdateBranchDialog) {
        BranchDialog(
            onDismissRequest = { showUpdateBranchDialog = false },
            isUpdate = true,
            viewModel = viewModel,
        )
    }
}

@Composable
internal fun BranchesScreen(
    modifier: Modifier = Modifier,
    uiState: StepperBusinessUiState,
    onAddBranch: () -> Unit,
    onUpdateClick: (Branch) -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            BranchesHeader(onAddClick = onAddBranch)
            BranchesList(branches = uiState.branches, onUpdateClick = onUpdateClick)
        }
        Row(
            modifier =
                Modifier
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
                onClick = onNextClick,
                text = { Text(stringResource(id = R.string.feature_stepper_next_button_text)) },
                trainingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                        contentDescription = null,
                    )
                },
                modifier =
                    Modifier
                        .wrapContentSize(),
            )
        }
    }
}

@Preview
@Composable
fun BranchesScreenLoadingPreview() {
    POSTheme {
        PosBackground {
            BranchesScreen(
                uiState = StepperBusinessUiState(isLoading = true),
                onAddBranch = {},
                onUpdateClick = {},
                onNextClick = {},
            ) {
            }
        }
    }
}

@com.casecode.pos.core.ui.DevicePreviews
@Composable
fun BranchesScreenPreview() {
    POSTheme {
        PosBackground {
            BranchesScreen(
                uiState =
                StepperBusinessUiState(
                    branches =
                    arrayListOf(
                        Branch(
                            branchCode = 1,
                            branchName = "branch1",
                            phoneNumber = "0000000000",
                        ),
                        Branch(branchCode = 3, branchName = "branch2", phoneNumber = "0000000000"),
                        Branch(branchCode = 4, branchName = "branch3", phoneNumber = "0000000000"),
                        Branch(branchCode = 5, branchName = "branch4", phoneNumber = "0000000000"),
                        Branch(branchCode = 6, branchName = "branch5", phoneNumber = "0000000000"),
                        Branch(branchCode = 7, branchName = "branch6", phoneNumber = "0000000000"),
                        Branch(branchCode = 8, branchName = "branch7", phoneNumber = "0000000000"),
                        Branch(branchCode = 9, branchName = "branch8", phoneNumber = "0000000000"),
                    ),
                ),
                onAddBranch = {},
                onUpdateClick = {},
                onNextClick = {},
            ) {
            }
        }
    }
}