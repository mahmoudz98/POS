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

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.casecode.pos.core.model.data.users.Item

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [Item] for Composable previews.
 */
class ItemsPreviewParameterProvider : PreviewParameterProvider<List<Item>> {
    override val values: Sequence<List<Item>>
        get() =
            sequenceOf(
                listOf(
                    Item(
                        name = "Iphone13",
                        unitPrice = 200.0,
                        quantity = 1,
                        imageUrl = "",
                        sku = "12345",
                    ),
                    Item(
                        name = "IPhone14",
                        unitPrice = 2000.0,
                        quantity = 11,
                        imageUrl = "",
                        sku = "12342256",
                    ),
                    Item(
                        name = "IPhone15",
                        unitPrice = 3000.0,
                        quantity = 1,
                        imageUrl = "",
                        sku = "1232456",
                    ),
                ),
            )
}