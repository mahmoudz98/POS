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
package com.casecode.pos.core.model.business

import kotlin.time.Instant

data class Business(
    val id: String = "",
    val name: String,
    val ownerUid: String,
    val vertical: Vertical,
    val companyCode: String = "",
    val currencyCode: String,
    val status: BusinessStatus,
    val email: String,
    val phone: String,
    val updatedAt: Instant,
    val createdAt: Instant,
)