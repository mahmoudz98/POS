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
package com.casecode.pos.feature.sales.report

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.ui.R

@ExperimentalMaterial3Api
@Composable
fun DatePickerView(
    modifier: Modifier = Modifier,
    onDataSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    currentSelectedDate: Long?,
) {
    val datePickerState =
        rememberDatePickerState(
            currentSelectedDate,
            selectableDates =
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis <= System.currentTimeMillis()
            },
        )

    DatePickerDialog(
        modifier = Modifier,
        onDismissRequest = { onDismiss() },
        confirmButton = {
            PosTextButton(
                onClick = {
                    onDataSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                },
            ) {
                Text(
                    text = stringResource(R.string.core_ui_dialog_ok_button_text),
                )
            }
        },
        dismissButton = {
            PosTextButton(
                onClick = {
                    onDismiss()
                },
            ) { Text(text = stringResource(R.string.core_ui_dialog_cancel_button_text)) }
        },
    ) {
        DatePicker(
            state = datePickerState,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun DatePickerViewPreview() {
    POSTheme {
        DatePickerView(
            onDataSelected = {},
            onDismiss = {},
            currentSelectedDate = null,
        )
    }
}