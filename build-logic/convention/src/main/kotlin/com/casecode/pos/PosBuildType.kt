package com.casecode.pos

enum class PosBuildType(
    val applicationIdSuffix: String? = null,
) {
    DEBUG(".debug"),
    RELEASE,
}