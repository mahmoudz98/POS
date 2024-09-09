package com.casecode.pos.benchmark

/**
 * Convenience parameter to use proper package name with regards to build type and build flavor.
 */
val PACKAGE_NAME = buildString {
    append("com.google.samples.apps.nowinandroid")
    append(BuildConfig.APP_FLAVOR_SUFFIX)
}
