plugins {
    alias(libs.plugins.pos.android.feature)
    alias(libs.plugins.pos.android.library.compose)
}

android {
    namespace = "com.casecode.pos.feature.item"

}

dependencies {
    implementation(projects.core.data)

    implementation(libs.accompanist.permissions)

    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)
}