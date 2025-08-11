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
package com.casecode.pos.core.firebase.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a supported currency with its localized names and properties.
 * Corresponds to an object in the 'supported_currencies_json' remote config array.
 */
@Serializable
data class SupportedCurrency(
    @SerialName("code")
    val code: String,

    @SerialName("name_en")
    val nameEn: String,

    @SerialName("name_ar")
    val nameAr: String,

    @SerialName("symbol")
    val symbol: String,

    @SerialName("decimalPlaces")
    val decimalPlaces: Int,
)