plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.gms.google.services) apply false
    alias(libs.plugins.secrets)
    alias(libs.plugins.gradle.cache.fix) apply false
    alias(libs.plugins.dependencyGuard) apply false
    alias(libs.plugins.module.graph) apply true
    alias(libs.plugins.power.assert) apply false
    // alias(libs.plugins.dependency.analysis) apply true
}

tasks.withType<JavaCompile>().configureEach {
    options.isIncremental = true
}