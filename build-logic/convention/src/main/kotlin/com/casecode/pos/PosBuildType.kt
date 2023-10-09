package com.casecode.pos

/**
 * This is shared between :app and :benchmarks module to provide configurations type safety.
 */
enum class PosBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE,
    BENCHMARK(".benchmark")
}
