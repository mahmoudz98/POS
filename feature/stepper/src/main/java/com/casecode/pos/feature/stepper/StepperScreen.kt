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
package com.casecode.pos.feature.stepper

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.Stepper
import com.casecode.pos.feature.stepper.branches.BranchesScreen
import com.casecode.pos.feature.stepper.employees.EmployeesStepperScreen
import com.casecode.pos.feature.stepper.subscriptions.BusinessSubscriptionScreen
import kotlinx.coroutines.launch
import com.casecode.pos.core.ui.R.string as uiString

@Composable
fun StepperScreen(
    viewModel: StepperBusinessViewModel = hiltViewModel(),
    onMoveToMainActivity: () -> Unit,
    onMoveToSignInActivity: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()
    var showClosingDialog by remember { mutableStateOf(false) }
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val buttonStepState by viewModel.buttonStepState.collectAsStateWithLifecycle()

    BackHandler {
        if (pagerState.currentPage > 0) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
            }
        } else {
            showClosingDialog = true
        }
    }
    if (showClosingDialog) {
        CloseBusinessStepDialog(
            onConfirm = {
                coroutineScope.launch {
                    val signOutJob = viewModel.signOut()
                    signOutJob.join()
                    if (signOutJob.isCompleted) {
                        onMoveToSignInActivity()
                    }
                }
                showClosingDialog = false
            },
            onDismiss = {
                showClosingDialog = false
            },
        )
    }

    LaunchedEffect(buttonStepState) {
        if (buttonStepState.buttonNextStep) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
            viewModel.restNextStep()
        }
        if (buttonStepState.buttonPreviousStep) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
            }
            viewModel.restPreviousStep()
        }
        if (buttonStepState.buttonCompletedSteps) {
            onMoveToMainActivity()
        }
    }

    StepperScreen(userMessage, pagerState, viewModel)
}

@Composable
private fun StepperScreen(
    userMessage: Int?,
    pagerState: PagerState = rememberPagerState(pageCount = { 4 }),
    viewModel: StepperBusinessViewModel,
) {
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    Box(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier =
            Modifier
                .systemBarsPadding()
                .wrapContentHeight(Alignment.Bottom)
                .zIndex(1f),
        )
        Column {
            val stepDescriptionList =
                listOf(
                    stringResource(id = uiString.core_ui_menu_business_info_title),
                    stringResource(id = uiString.core_ui_menu_branches_title),
                    stringResource(id = uiString.core_ui_subscription_plan_title),
                    stringResource(id = uiString.core_ui_employees_title),
                )

            Stepper(
                modifier = Modifier.fillMaxWidth(),
                numberOfSteps = pagerState.pageCount,
                currentStep = pagerState.currentPage + 1,
                stepDescriptionList = stepDescriptionList,
                selectedColor = MaterialTheme.colorScheme.primary,
                onSelectedColor = MaterialTheme.colorScheme.onPrimary,
                unSelectedColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalPager(
                userScrollEnabled = false,
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                when (page) {
                    BUSINESS_INFO_STEP -> BusinessInfoScreen(viewModel = viewModel)
                    BRANCHES_STEP -> BranchesScreen(viewModel = viewModel)
                    SUBSCRIPTION_STEP -> BusinessSubscriptionScreen(viewModel = viewModel)
                    EMPLOYEES_STEP -> EmployeesStepperScreen(viewModel = viewModel)
                }
            }
        }
    }

    userMessage?.let { message ->
        val snackbarText = stringResource(message)
        LaunchedEffect(snackbarHostState, message, snackbarText) {
            snackbarHostState.showSnackbar(snackbarText)
            viewModel.snackbarMessageShown()
        }
    }
}

@Composable
fun CloseBusinessStepDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.feature_stepper_dialog_exit_title))
        },
        text = {
            Text(text = stringResource(id = R.string.feature_stepper_dialog_exit_message))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.feature_stepper_dialog_exit_yes_button_text))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = string.core_ui_dialog_cancel_button_text))
            }
        },
    )
}

private const val BUSINESS_INFO_STEP = 0
private const val BRANCHES_STEP = 1
private const val SUBSCRIPTION_STEP = 2
private const val EMPLOYEES_STEP = 3