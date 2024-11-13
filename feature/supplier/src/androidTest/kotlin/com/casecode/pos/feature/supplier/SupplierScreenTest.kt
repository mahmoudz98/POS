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
package com.casecode.pos.feature.supplier

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.casecode.pos.core.designsystem.component.SearchWidgetState
import com.casecode.pos.core.testing.data.suppliersTestData
import kotlin.test.Test
import org.junit.Rule

class SupplierScreenTest {
    @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun circularProgressIndicator_whenScreenIsLoading_exists() {
        composeTestRule.setContent {
            SupplierScreen(
                supplierUiState = SuppliersUiState.Loading,
                filteredSuppliers = emptyList(),
                searchWidgetState = SearchWidgetState.CLOSED,
                searchQuery = "",
                userMessage = null,
                countryIsoCode = "EG",
                onBackClick = {},
                onSearchClicked = {},
                onSearchQueryChanged = {},
                onClearRecentSearches = {},
                onMessageShown = {},
                onAddSupplierClick = {},
                onSupplierClick = {},
                onDeleteSupplierClick = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("LoadingSuppliers").assertExists()
    }

    @Test
    fun emptySupplierScreen_whenEmptyState_exists() {
        composeTestRule.setContent {
            SupplierScreen(
                supplierUiState = SuppliersUiState.Empty,
                filteredSuppliers = emptyList(),
                searchWidgetState = SearchWidgetState.CLOSED,
                searchQuery = "",
                userMessage = null,
                countryIsoCode = "EG",
                onBackClick = {},
                onSearchClicked = {},
                onSearchQueryChanged = {},
                onClearRecentSearches = {},
                onMessageShown = {},
                onAddSupplierClick = {},
                onSupplierClick = {},
                onDeleteSupplierClick = {},
            )
        }

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(R.string.feature_supplier_empty_title),
            )
            .assertExists()
    }

    @Test
    fun suppliers_whenSuccessState_isShown() {
        val testSuppliersData = suppliersTestData
        composeTestRule.setContent {
            SupplierScreen(
                supplierUiState = SuppliersUiState.Success(testSuppliersData),
                filteredSuppliers = testSuppliersData,
                searchWidgetState = SearchWidgetState.CLOSED,
                searchQuery = "",
                userMessage = null,
                countryIsoCode = "EG",
                onBackClick = {},
                onSearchClicked = {},
                onSearchQueryChanged = {},
                onClearRecentSearches = {},
                onMessageShown = {},
                onAddSupplierClick = {},
                onSupplierClick = {},
                onDeleteSupplierClick = {},
            )
        }
        composeTestRule.onNodeWithText(testSuppliersData[0].contactName).assertExists()
        composeTestRule.onNodeWithText(testSuppliersData[1].contactName).assertExists()
        composeTestRule.onNodeWithText(testSuppliersData[2].contactName).assertExists()
    }
}