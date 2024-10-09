package com.casecode.pos

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.powerassert.gradle.PowerAssertGradleExtension

@OptIn(ExperimentalKotlinGradlePluginApi::class)
internal fun PowerAssertGradleExtension.configurePowerAssert() {
    functions.set(
        setOf(
            "kotlin.assert",
            "kotlin.test.assertTrue",
            "kotlin.test.assertEquals",
            "kotlin.require",
            "org.junit.Rule",
        ),
    )
    includedSourceSets.set(
        setOf(
            "demoDebug",
            "demoDebugUnitTest",
            "demoReleaseUnitTest",
            "prodDebugUnitTest",
            "prodReleaseUnitTest",
        ),
    )

}