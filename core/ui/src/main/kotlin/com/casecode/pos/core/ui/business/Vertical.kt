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
package com.casecode.pos.core.ui.business

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.casecode.pos.core.model.business.Vertical
import com.casecode.pos.core.ui.R

@Composable
fun Vertical.toDisplayString(): String {
    return stringResource(
        when (this) {
            Vertical.RETAIL -> R.string.core_ui_business_vertical_retail
            Vertical.CAFE -> R.string.core_ui_business_vertical_cafe
            Vertical.PHARMACY -> R.string.core_ui_business_vertical_pharmacy
        },
    )
}
