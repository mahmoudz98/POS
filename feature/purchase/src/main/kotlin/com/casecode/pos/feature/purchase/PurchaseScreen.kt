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
package com.casecode.pos.feature.purchase

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.icon.PosIcons

@Composable
fun PurchaseScreen(
    modifier: Modifier = Modifier,
    onBillsScreenClick: () -> Unit = {},
    onSupplierScreenClick: () -> Unit = {},
) {
    Column(modifier = modifier.padding(16.dp)) {
        PosTextButton(
            onClick = onBillsScreenClick,
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.feature_purchase_bills_button_text),
            leadingIcon = PosIcons.Bill,
        )
        PosTextButton(
            onClick = onSupplierScreenClick,
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.feature_purchase_suppliers_button_text),
            leadingIcon = PosIcons.Supplier,
        )
    }
}