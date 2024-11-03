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
package com.casecode.pos.core.testing.data

import com.casecode.pos.core.model.data.users.Invoice
import com.casecode.pos.core.model.data.users.InvoiceGroup
import com.casecode.pos.core.model.data.users.Item

val invoicesTestData =
    listOf(
        Invoice(
            items =
            arrayListOf(
                Item(
                    name = "item #1",
                    unitPrice = 1.0,
                    quantity = 23,
                    sku = "1234567899090",
                    imageUrl = "www.image1.png",
                ),
                Item(
                    "item #2",
                    unitPrice = 3.0,
                    quantity = 4,
                    sku = "1555567899090",
                    imageUrl = "www.image2.png",
                ),
                Item(
                    "item #2",
                    unitPrice = 3.0,
                    quantity = 0,
                    sku = "1200",
                    imageUrl = "www.image2.png",
                ),
            ),
        ),
        Invoice(
            items =
            arrayListOf(
                Item(
                    name = "item #1",
                    unitPrice = 1.0,
                    quantity = 23,
                    sku = "1234567899090",
                    imageUrl = "www.image1.png",
                ),
                Item(
                    "item #2",
                    unitPrice = 3.0,
                    quantity = 4,
                    sku = "1555567899090",
                    imageUrl = "www.image2.png",
                ),
                Item(
                    "item #2",
                    unitPrice = 3.0,
                    quantity = 0,
                    sku = "1200",
                    imageUrl = "www.image2.png",
                ),
            ),
        ),
        Invoice(
            items =
            arrayListOf(
                Item(
                    name = "item #1",
                    unitPrice = 1.0,
                    quantity = 23,
                    sku = "1234567899090",
                    imageUrl = "www.image1.png",
                ),
                Item(
                    "item #2",
                    unitPrice = 3.0,
                    quantity = 4,
                    sku = "1555567899090",
                    imageUrl = "www.image2.png",
                ),
                Item(
                    "item #2",
                    unitPrice = 3.0,
                    quantity = 0,
                    sku = "1200",
                    imageUrl = "www.image2.png",
                ),
            ),
        ),
        Invoice(
            items =
            arrayListOf(
                Item(
                    name = "item #1",
                    unitPrice = 1.0,
                    quantity = 23,
                    sku = "1234567899090",
                    imageUrl = "www.image1.png",
                ),
                Item(
                    "item #2",
                    unitPrice = 3.0,
                    quantity = 4,
                    sku = "1555567899090",
                    imageUrl = "www.image2.png",
                ),
                Item(
                    "item #2",
                    unitPrice = 3.0,
                    quantity = 0,
                    sku = "1200",
                    imageUrl = "www.image2.png",
                ),
            ),
        ),
    )
val invoicesGroupTestData =
    listOf(
        InvoiceGroup(
            "22-12-2023",
            listOf(
                Invoice(
                    items =
                    arrayListOf(
                        Item(
                            name = "item #1",
                            unitPrice = 1.0,
                            quantity = 23,
                            sku = "1234567899090",
                            imageUrl = "www.image1.png",
                        ),
                        Item(
                            "item #2",
                            unitPrice = 3.0,
                            quantity = 4,
                            sku = "1555567899090",
                            imageUrl = "www.image2.png",
                        ),
                        Item(
                            "item #2",
                            unitPrice = 3.0,
                            quantity = 0,
                            sku = "1200",
                            imageUrl = "www.image2.png",
                        ),
                    ),
                ),
            ),
        ),
        InvoiceGroup(
            "2-11-2023",
            listOf(
                Invoice(
                    items =
                    arrayListOf(
                        Item(
                            name = "item #1",
                            unitPrice = 1.0,
                            quantity = 23,
                            sku = "1234567899090",
                            imageUrl = "www.image1.png",
                        ),
                        Item(
                            "item #2",
                            unitPrice = 3.0,
                            quantity = 4,
                            sku = "1555567899090",
                            imageUrl = "www.image2.png",
                        ),
                        Item(
                            "item #2",
                            unitPrice = 3.0,
                            quantity = 0,
                            sku = "1200",
                            imageUrl = "www.image2.png",
                        ),
                    ),
                ),
            ),
        ),
        InvoiceGroup(
            "2-2-2024",
            listOf(
                Invoice(
                    items =
                    arrayListOf(
                        Item(
                            name = "item #1",
                            unitPrice = 1.0,
                            quantity = 23,
                            sku = "1234567899090",
                            imageUrl = "www.image1.png",
                        ),
                        Item(
                            "item #2",
                            unitPrice = 3.0,
                            quantity = 4,
                            sku = "1555567899090",
                            imageUrl = "www.image2.png",
                        ),
                        Item(
                            "item #2",
                            unitPrice = 3.0,
                            quantity = 0,
                            sku = "1200",
                            imageUrl = "www.image2.png",
                        ),
                    ),
                ),
            ),
        ),
        InvoiceGroup(
            "22-4-2024",
            listOf(
                Invoice(
                    items =
                    arrayListOf(
                        Item(
                            name = "item #1",
                            unitPrice = 1.0,
                            quantity = 23,
                            sku = "1234567899090",
                            imageUrl = "www.image1.png",
                        ),
                        Item(
                            "item #2",
                            unitPrice = 3.0,
                            quantity = 4,
                            sku = "1555567899090",
                            imageUrl = "www.image2.png",
                        ),
                        Item(
                            "item #2",
                            unitPrice = 3.0,
                            quantity = 1,
                            sku = "1200",
                            imageUrl = "www.image2.png",
                        ),
                    ),
                ),
            ),
        ),
    )