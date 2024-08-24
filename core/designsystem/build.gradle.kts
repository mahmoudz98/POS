plugins {
    alias(libs.plugins.pos.android.library)
    alias(libs.plugins.pos.android.library.compose)
}

android {
    namespace = "com.casecode.pos.core.designsystem"
}

dependencies {

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material3.adaptive)
    api(libs.androidx.compose.material3.navigationSuite)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui.util)

    implementation(libs.core)
    implementation(libs.coil.kt.compose)
    implementation(libs.androidx.constraintlayout)

    // testImplementation(libs.androidx.compose.ui.test)
    // testImplementation(libs.androidx.compose.ui.testManifest)

    testImplementation(libs.hilt.android.testing)
}