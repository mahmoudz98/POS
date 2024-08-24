plugins {
    alias(libs.plugins.pos.android.library)
    alias(libs.plugins.pos.hilt)
}

android {
    namespace = "com.casecode.pos.core.domain"
}

dependencies {
    api(projects.core.model)
    implementation(libs.javax.inject)
    implementation(libs.coroutines.core)
    testImplementation(libs.coroutines.test)
    testImplementation(projects.core.testing)

    testImplementation(libs.test.hamcrest)
    testCompileOnly(libs.test.hamcrest.library)
}