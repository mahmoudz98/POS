plugins {
    alias(libs.plugins.pos.android.feature)
    alias(libs.plugins.pos.android.library.compose)
}
android {
    namespace = "com.casecode.pos.feature.signin"

}

dependencies {
    implementation(projects.core.data)
    implementation(projects.feature.loginEmployee)

    implementation(libs.googleid)
    //implementation(libs.androidx.credentials)

    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
    androidTestImplementation(projects.core.testing)

}