package com.casecode.pos.core.ui.utils

fun Double?.toFormattedString(): String =
    this?.toBigDecimal()?.stripTrailingZeros()?.toPlainString() ?: ""