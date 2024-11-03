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
package com.casecode.pos.feature.item

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.feature.item.R.string
import com.casecode.pos.feature.item.details.AddOrUpdateItemScreen
import org.junit.Rule
import org.junit.Test
import com.casecode.pos.core.ui.R.string as uiString

class AddOrUpdateItemScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private val saveButtonLabel by lazy {
        composeTestRule.activity.getString(string.feature_item_save_action_text)
    }
    private val trackStockChipLabel by lazy {
        composeTestRule.activity.getString(string.feature_item_not_track_stock_chip_label)
    }

    @Test
    fun whenItemInputEmpty_thenShowAllErrors() {
        composeTestRule.setContent {
            AddOrUpdateItemScreen(
                isUpdate = false,
                itemUpdated = null,
                userMessage = null,
                categories = emptySet(),
                onNavigateBack = {},
                onUpdateImageItem = {},
                showSnackbarMessage = {},
                onMessageSnackbarShown = {},
                onAddItem = { _, _ -> },
                onUpdateItem = { _, _ -> },
            )
        }

        composeTestRule.onNodeWithText(saveButtonLabel).performClick()

        assertErrorDisplayed(string.feature_item_error_name_empty)
        assertErrorDisplayed(string.feature_item_error_sku_empty)
        assertErrorDisplayed(string.feature_item_error_price_empty)
    }

    @Test
    fun whenClickTrackStock_thenShowQuantityInputs() {
        composeTestRule.setContent {
            AddOrUpdateItemScreen(
                isUpdate = false,
                itemUpdated = null,
                userMessage = null,
                categories = emptySet(),
                onNavigateBack = {},
                onUpdateImageItem = {},
                showSnackbarMessage = {},
                onMessageSnackbarShown = {},
                onAddItem = { _, _ -> },
                onUpdateItem = { _, _ -> },
            )
        }

        composeTestRule
            .onNodeWithText(trackStockChipLabel)
            .performScrollTo()
            .performClick()

        assertTextDisplayed(uiString.core_ui_item_quantity_label)
        assertTextDisplayed(uiString.core_ui_item_reorder_level_label)
        assertTextDisplayed(uiString.core_ui_item_qty_per_pack_label)
    }

    @Test
    fun whenSaveButtonClicked_onAddItemCalled_withValidInput() {
        var addItemCalled = false

        composeTestRule.setContent {
            AddOrUpdateItemScreen(
                isUpdate = false,
                itemUpdated = null,
                userMessage = null,
                categories = emptySet(),
                onNavigateBack = {},
                onUpdateImageItem = {},
                showSnackbarMessage = {},
                onMessageSnackbarShown = {},
                onAddItem = { _, _ -> addItemCalled = true },
                onUpdateItem = { _, _ -> },
            )
        }

        fillValidInputs()
        composeTestRule.onNodeWithText(saveButtonLabel).performClick()

        assert(addItemCalled) { "onAddItem should be called with valid input" }
    }

    @Test
    fun whenSaveButtonClicked_onUpdateItemCalled_withValidInput() {
        var updateItemCalled = false

        composeTestRule.setContent {
            AddOrUpdateItemScreen(
                isUpdate = true,
                itemUpdated = Item("Test", "Category", unitPrice = 10.0, sku = "123"),
                userMessage = null,
                categories = emptySet(),
                onNavigateBack = {},
                onUpdateImageItem = {},
                showSnackbarMessage = {},
                onMessageSnackbarShown = {},
                onAddItem = { _, _ -> },
                onUpdateItem = { _, _ -> updateItemCalled = true },
            )
        }

        fillValidInputsUpdate()
        composeTestRule.onNodeWithText(saveButtonLabel).performClick()

        assert(updateItemCalled) { "onUpdateItem should be called with valid input" }
    }

    private fun assertErrorDisplayed(stringRes: Int) {
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(stringRes))
            .assertIsDisplayed()
    }

    private fun assertTextDisplayed(stringRes: Int) {
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(stringRes))
            .performScrollTo()
            .assertIsDisplayed()
    }

    private fun fillValidInputs() {
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(string.feature_item_name_hint),
            ).performTextInput("Test Item")

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(string.feature_item_barcode_hint),
            ).performTextInput("123")

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(string.feature_item_price_hint),
            ).performTextInput("100.0")
    }

    private fun fillValidInputsUpdate() {
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(string.feature_item_name_hint),
            ).performTextInput("Test Item")

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(string.feature_item_price_hint),
            ).performTextInput("100.0")
    }
}