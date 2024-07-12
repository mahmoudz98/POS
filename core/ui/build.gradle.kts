plugins {
    alias(libs.plugins.pos.android.library)
    alias(libs.plugins.pos.android.library.compose)
}

android {
    namespace = "com.casecode.pos.core.ui"

}

dependencies {
    api(projects.core.designsystem)
    api(projects.core.model)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation (libs.libphonenumber)

    api(libs.zxing.android.embedded)

}