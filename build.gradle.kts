
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
   // alias(libs.plugins.dependency.analysis) apply true

}
/*moduleGraphAssert {
    maxHeight = 4
    allowed = arrayOf(":.* -> :core', ':feature.* -> :lib.")
    restricted = arrayOf(":feature-[a-z]* -X> :forbidden-to-depend-on")
    assertOnAnyBuild = true
}*/
tasks.withType<JavaCompile>().configureEach {
    options.isIncremental = true
}

// TODo: use gradle profile https://developer.android.com/build/profile-your-build#getting_started
// TODO: Add Dependency graph let visualize dependencies in a graph.