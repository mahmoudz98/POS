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
package com.casecode.pos.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.component.scrollbar.DraggableScrollbar
import com.casecode.pos.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.casecode.pos.core.designsystem.component.scrollbar.scrollbarState
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.business.Branch

@Composable
fun BranchDialogContent(
    branchName: String,
    hasNameError: Boolean,
    onNameChange: (String) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    phoneError: Int?,
    focusRequester: FocusRequester,
    onDoneClickTriggered: () -> Unit,
) {
    Column {
        PosOutlinedTextField(
            value = branchName,
            onValueChange = {
                onNameChange(it)
            },
            isError = hasNameError,
            label = stringResource(id = R.string.core_ui_branch_name_hint),
            keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            supportingText = if (hasNameError) {
                stringResource(
                    R.string.core_ui_error_branch_name_empty,
                )
            } else {
                null
            },
            modifier =
            Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
        )
        Spacer(modifier = Modifier.height(8.dp))
        PosOutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                onPhoneNumberChange(it)
            },
            isError = phoneError != null,
            supportingText = phoneError?.let { stringResource(it) },
            keyboardActions =
            KeyboardActions(
                onDone = {
                    onDoneClickTriggered()
                },
            ),
            keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done,
            ),
            label = stringResource(id = R.string.core_ui_hint_phone_number_branch),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun BranchesList(
    branches: List<Branch>,
    onUpdateClick: (Branch) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        val scrollableState = rememberLazyListState()
        LazyColumn(
            modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            state = scrollableState,
        ) {
            branches.forEach { branch ->
                item(key = branch.id) {
                    BranchItem(branch = branch, onUpdateClick)
                }
            }
        }
        val scrollbarState =
            scrollableState.scrollbarState(
                itemsAvailable = branches.size,
            )
        scrollableState.DraggableScrollbar(
            modifier =
            Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved =
            scrollableState.rememberDraggableScroller(
                itemsAvailable = branches.size,
            ),
        )
    }
}

@Composable
fun BranchItem(branch: Branch, onUpdateClick: (Branch) -> Unit = {}) {
    ElevatedCard(
        Modifier
            .padding(bottom = 8.dp)
            .clickable { onUpdateClick(branch) },
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(R.string.core_ui_branch_name_hint) + branch.name,
                )
            },
            supportingContent = {
                Text(text = branch.phone)
            },
            leadingContent = {
                Text(
                    text = "0${branch.id}",
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            colors =
            ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                headlineColor = MaterialTheme.colorScheme.onSurfaceVariant,
                overlineColor = MaterialTheme.colorScheme.onSurfaceVariant,
                supportingColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}

@DevicePreviews
@Composable
fun BranchItemPreview() {
    POSTheme {
        BranchesList(
            branches =
            arrayListOf(
                Branch(
                    id = "1",
                    name = "branch1",
                    phone = "21213123",
                ),

            ),
            {},
        )
    }
}
