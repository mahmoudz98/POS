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

import com.casecode.pos.core.model.business.Branch

val branchesTestData = listOf(
    Branch(
        id = "branch1",
        name = "Main Branch",
        phone = "1234567890",
    ),
    Branch(
        id = "branch2",
        name = "Branch 2",
        phone = "9876543210",
    ),
    Branch(
        id = "branch3",
        name = "Branch 3",
        phone = "5555555555",
    ),
)
