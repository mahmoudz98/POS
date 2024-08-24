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
                        price = 200.0,
                        quantity = 1.0,
                        imageUrl = "",
                        sku = "12345",
                    ),
                    Item(
                        name = "IPhone14",
                        price = 2000.0,
                        quantity = 112.0,
                        imageUrl = "",
                        sku = "12342256",
                    ),
                    Item(
                        name = "IPhone15",
                        price = 3000.0,
                        quantity = 1.0,
                        imageUrl = "",
                        sku = "1232456",
                    ),
                ),
            )
}