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

import com.casecode.pos.core.model.data.users.Supplier

val suppliersTestData =
    listOf(
        Supplier(
            id = "1",
            companyName = "Supplier A",
            contactName = "John Doe",
            contactEmail = "john.doe@supplierA.com",
            contactPhone = "123-456-7890",
            address = "123 Main St, Anytown",
            category = "Electronics",
        ),
        Supplier(
            id = "2",
            companyName = "Supplier B",
            contactName = "Jane Smith",
            contactEmail = "jane.smith@supplierB.com",
            contactPhone = "987-654-3210",
            address = "456 Oak Ave, Anytown",
            category = "Food & Beverage",
        ),
        Supplier(
            id = "3",
            companyName = "Supplier C",
            contactName = "Peter Jones",
            contactEmail = "peter.jones@supplierC.com",
            contactPhone = "555-123-4567",
            address = "789 Pine St, Anytown",
            category = "Clothing",
        ),
    )