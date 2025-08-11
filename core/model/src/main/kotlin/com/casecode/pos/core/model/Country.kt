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
package com.casecode.pos.core.model

data class Country(
    val countryCode: String,
    val nameEn: String,
    val nameAr: String,
    val phoneCode: String,
)
fun getSupportedCountries(): List<Country> = listOf(
    Country(
        countryCode = "EG",
        nameEn = "Egypt",
        nameAr = "مصر",
        phoneCode = "+20",
    ),
    Country(
        countryCode = "SA",
        nameEn = "Saudi Arabia",
        nameAr = "السعودية",
        phoneCode = "+966",
    ),
    Country(
        countryCode = "AE",
        nameEn = "UAE",
        nameAr = "الإمارات",
        phoneCode = "+971",
    ),
    Country(
        countryCode = "US",
        nameEn = "United States",
        nameAr = "الولايات المتحدة",
        phoneCode = "+1",
    ),
    Country(
        countryCode = "CA",
        nameEn = "Canada",
        nameAr = "كندا",
        phoneCode = "+1",
    ),
    Country(
        countryCode = "GB",
        nameEn = "United Kingdom",
        nameAr = "المملكة المتحدة",
        phoneCode = "+44",
    ),
)