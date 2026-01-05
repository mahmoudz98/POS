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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.business.Branch

@Composable
fun BranchSelectionDialog(
    branches: List<Branch>,
    onBranchSelected: (Branch) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.core_ui_dialog_title_select_branch)) },
        text = {
            Box(Modifier.fillMaxWidth()) {
                LazyColumn {
                    items(branches) { branch ->
                        ListItem(
                            headlineContent = { Text(branch.name) },
                            supportingContent = { Text(branch.phone) },
                            modifier = Modifier
                                .clickable { onBranchSelected(branch) }
                                .padding(vertical = 4.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(android.R.string.cancel))
            }
        },
    )
}

@Preview
@Composable
fun BranchSelectionDialogPreview() {
    POSTheme {
        BranchSelectionDialog(
            branches = listOf(
                Branch(name = "Main Branch", phone = "123", id = "1"),
                Branch(name = "Second Branch", phone = "456", id = "2"),
            ),
            onBranchSelected = {},
            onDismissRequest = {},
        )
    }
}
