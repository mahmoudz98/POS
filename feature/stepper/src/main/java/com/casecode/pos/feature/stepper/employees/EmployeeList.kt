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
package com.casecode.pos.feature.stepper.employees

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.component.scrollbar.DraggableScrollbar
import com.casecode.pos.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.casecode.pos.core.designsystem.component.scrollbar.scrollbarState
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Employee

@Composable
internal fun EmployeesList(
    modifier: Modifier = Modifier,
    employees: List<Employee>,
    onEmployeeClick: (Employee) -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        val scrollableState = rememberLazyListState()

        LazyColumn(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            state = scrollableState,
        ) {
            employees.forEach { employee ->
                item(key = employee.name) {
                    EmployeeItem(
                        employee = employee,
                        onItemClick = { onEmployeeClick(employee) },
                    )
                }
            }
        }
        val scrollbarState =
            scrollableState.scrollbarState(
                itemsAvailable = employees.size,
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
                itemsAvailable = employees.size,
            ),
        )
    }
}

@Composable
private fun EmployeeItem(
    employee: Employee,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
) {
    ElevatedCard(
        modifier
            .padding(bottom = 8.dp)
            .clickable {
                onItemClick()
            },
    ) {
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
        )
    }
}

@com.casecode.pos.core.ui.DevicePreviews
@Composable
fun EmployeeListPreview() {
    POSTheme {
        EmployeesList(
            employees =
            listOf(
                Employee(
                    name = "Henry Harvey",
                    phoneNumber = "(723) 178-3587",
                    password = null,
                    branchName = null,
                    permission = "posuere",
                ),
                Employee(
                    name = "Louise Bass",
                    phoneNumber = "(373) 204-1461",
                    password = null,
                    branchName = null,
                    permission = "habitasse",
                ),
            ),
        ) { _ -> }
    }
}