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
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.casecode.pos.core.model.data.users.Supplier
import org.junit.Rule
import kotlin.test.Test
import com.casecode.pos.core.ui.R.string as uiString

class SupplierDialogTest {
    @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    val add by lazy {
        composeTestRule.activity.getString(R.string.feature_supplier_add_button_text)
    }
    val update by lazy {
        composeTestRule.activity.getString(R.string.feature_supplier_update_button_text)
    }

    @Test
    fun whenSupplierInputEmpty_thenShowError() {
        composeTestRule.setContent {
            SupplierDialog(
                isUpdate = false,
                supplierUpdate = null,
                countryIsoCode = "EG",
                onAddSupplier = { _ -> },
                onUpdateSupplier = { _ -> },
                onDismiss = {},
            )
        }
        composeTestRule.onNodeWithText(add).performClick()
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(R.string.feature_supplier_error_name_empty),
            )
            .assertExists()
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(
                    R.string.feature_supplier_error_company_name_empty,
                ),
            )
            .assertExists()
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(uiString.core_ui_error_phone_empty))
            .assertExists()
    }

    @Test
    fun whenInputSupplierValidate_thenAssertErrorValidationNotExist() {
        composeTestRule.setContent {
            SupplierDialog(
                isUpdate = false,
                supplierUpdate = null,
                countryIsoCode = "EG",
                onAddSupplier = {},
                onUpdateSupplier = {},
                onDismiss = {},
            )
        }
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.feature_supplier_name_hint))
            .performTextInput("Supplier Name")
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(R.string.feature_supplier_company_name_hint),
            )
            .performTextInput("Company Name")
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(uiString.core_ui_work_phone_number_hint),
            )
            .performTextInput("1234567890")

        composeTestRule.onNodeWithText(add).performClick()
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(R.string.feature_supplier_error_name_empty),
            )
            .assertDoesNotExist()
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(
                    R.string.feature_supplier_error_company_name_empty,
                ),
            )
            .assertDoesNotExist()
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(uiString.core_ui_error_phone_invalid),
            )
            .assertDoesNotExist()
    }

    @Test
    fun givenSupplier_whenUpdate_thenAssertSupplierInputExist() {
        val supplier = Supplier(
            contactName = "Supplier Name",
            companyName = "Company Name",
            contactPhone = "1234567890",
            contactEmail = "william.henry.harrison@example-pet-store.com",
            address = "Address",
            category = "Category",
        )

        composeTestRule.setContent {
            SupplierDialog(
                isUpdate = true,
                supplierUpdate = supplier,
                countryIsoCode = "EG",
                onAddSupplier = {},
                onUpdateSupplier = {},
                onDismiss = {},
            )
        }
        composeTestRule.onNodeWithText(supplier.contactName).assertExists()
        composeTestRule.onNodeWithText(supplier.contactName).assertExists()
        composeTestRule.onNodeWithText(supplier.contactPhone).assertExists()
        composeTestRule.onNodeWithText(supplier.contactEmail).assertExists()
        composeTestRule.onNodeWithText(supplier.address).assertExists()
        composeTestRule.onNodeWithText(supplier.category).assertExists()
    }
}