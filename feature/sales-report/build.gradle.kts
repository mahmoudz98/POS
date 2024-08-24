plugins {
    alias(libs.plugins.pos.android.feature)
    alias(libs.plugins.pos.android.library.compose)
}

android {
    namespace = "com.casecode.pos.feature.sales_report"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)
}