plugins {
    alias(libs.plugins.pos.android.feature)
    alias(libs.plugins.pos.android.library.compose)
}

android {
    namespace = "com.casecode.pos.feature.login_employee"
}

dependencies {
    implementation(projects.core.data)

    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)
}