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

import com.casecode.pos.core.model.data.users.Item

val itemsTestData =
    listOf(
        Item(
            name = "Iphone1",
            unitPrice = 1.0,
            category = "Phones",
            quantity = 23,
            reorderLevel = 30,
            sku = "1234567899090",
            imageUrl = "www.image1.png",
        ),
        Item(
            "Samsung S2",
            category = "Phones",

            unitPrice = 3000.0,
            quantity = 4,
            reorderLevel = 5,
            sku = "1555567899090",
            imageUrl = "www.image2.png",
        ),
        Item(
            "Hp Envy",
            category = "Laptops",

            unitPrice = 3.0,
            quantity = 0,
            sku = "13123200",
            imageUrl = "www.image2.png",
        ),
        Item(
            "Macbook pro M3",
            category = "Laptops",

            unitPrice = 3.0,
            quantity = 0,
            reorderLevel = 1,
            sku = "1212312300",
            imageUrl = "www.image2.png",
        ),
        Item(
            "Airpods V1",
            category = "headphones",

            unitPrice = 3.0,
            quantity = 0,

            sku = "122300",
            imageUrl = "www.image2.png",
        ),
        Item(
            "Airpods V2",
            category = "headphones",
            unitPrice = 3.0,
            quantity = 0,
            reorderLevel = 0,
            sku = "120033",
            imageUrl = "www.image2.png",
        ),
    )