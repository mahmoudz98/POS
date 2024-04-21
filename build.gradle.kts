buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    //  base plugins
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.test) apply false

    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt) apply false
    // alias(libs.plugins.ksp) apply false

    alias(libs.plugins.gms.google.services) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.secrets)
    alias(libs.plugins.gradle.cache.fix) apply false
    alias(libs.plugins.navigation.safeargs.kotlin) apply false

    alias(libs.plugins.module.graph) apply true // Plugin applied to allow module graph generation

}
// Task to print all the module paths in the project e.g. :data
// Used by module graph generator script
tasks.register("printModulePaths") {
    subprojects {
        if (subprojects.size == 0) {
            println(this.path)
        }
    }
}
tasks.withType<Test>().configureEach {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
}
tasks.withType<Test>().configureEach {
    forkEvery = 100
}

// TODo: use gradle profile https://developer.android.com/build/profile-your-build#getting_started
// TODO: Add Dependency graph let visualize dependencies in a graph.