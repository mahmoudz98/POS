package com.casecode.pos.core.model

data class Country(
    val countryCode: String,
    val nameEn: String,
    val nameAr: String,
    val phoneCode: String
)
fun getSupportedCountries(): List<Country> = listOf(
    Country(
        countryCode = "EG",
        nameEn = "Egypt",
        nameAr = "مصر",
        phoneCode = "+20"
    ),
    Country(
        countryCode = "SA",
        nameEn = "Saudi Arabia",
        nameAr = "السعودية",
        phoneCode = "+966"
    ),
    Country(
        countryCode = "AE",
        nameEn = "UAE",
        nameAr = "الإمارات",
        phoneCode = "+971"
    ),
    Country(
        countryCode = "US",
        nameEn = "United States",
        nameAr = "الولايات المتحدة",
        phoneCode = "+1"
    ),
    Country(
        countryCode = "CA",
        nameEn = "Canada",
        nameAr = "كندا",
        phoneCode = "+1"
    ),
    Country(
        countryCode = "GB",
        nameEn = "United Kingdom",
        nameAr = "المملكة المتحدة",
        phoneCode = "+44"
    )
)
