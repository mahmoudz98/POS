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
    val decimalPlaces: Int
)
