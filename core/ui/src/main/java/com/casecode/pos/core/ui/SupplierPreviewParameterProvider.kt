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
import com.casecode.pos.core.model.data.users.Supplier

class SupplierPreviewParameterProvider : PreviewParameterProvider<List<Supplier>> {
    override val values: Sequence<List<Supplier>>
        get() =
            sequenceOf(
                listOf(
                    Supplier(
                        companyName = "Company Name",
                        contactName = "Contact Name",
                        contactPhone = "01234567891",
                        id = "1",
                        contactEmail = "email",
                        address = "",
                        category = "",
                    ),
                    Supplier(
                        companyName = "Company Name",
                        contactName = "Contact Name",
                        contactPhone = "012345678920",
                        id = "12323",
                        contactEmail = "email",
                        address = "",
                        category = "",
                    ),

                    Supplier(
                        companyName = "Company Name",
                        contactName = "Contact Name",
                        contactPhone = "01234567890",
                        id = "231",
                        contactEmail = "email",
                        address = "",
                        category = "",
                    ),
                    Supplier(
                        companyName = "Company Name",
                        contactName = "Contact Name",
                        contactPhone = "123456789",
                        id = "122",
                        contactEmail = "email",
                        address = "",
                        category = "",
                    ),
                    Supplier(
                        companyName = "Company Name",
                        contactName = "Contact Name",
                        contactPhone = "123-456-7890",
                        id = "14444",
                        contactEmail = "email",
                        address = "",
                        category = "",
                    ),
                    Supplier(
                        companyName = "Company Name",
                        contactName = "Contact Name",
                        contactPhone = "123-456-7890",
                        id = "4",
                        contactEmail = "email",
                        address = "",
                        category = "",
                    ),
                    Supplier(
                        companyName = "Company Name",
                        contactName = "Contact Name",
                        contactPhone = "123-456-7890",
                        id = "5",
                        contactEmail = "email",
                        address = "",
                        category = "",
                    ),
                    Supplier(
                        companyName = "Company Name",
                        contactName = "Contact Name",
                        contactPhone = "123-456-7890",
                        id = "6",
                        contactEmail = "email",
                        address = "",
                        category = "",
                    ),
                    Supplier(
                        companyName = "Company Name",
                        contactName = "Contact Name",
                        contactPhone = "123-456-7890",
                        id = "7",
                        contactEmail = "email",
                        address = "",
                        category = "",
                    ),
                ),
            )
}