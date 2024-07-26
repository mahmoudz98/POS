plugins {
    alias(libs.plugins.pos.android.feature)
    alias(libs.plugins.pos.android.library.compose)
}

android {
    namespace = "com.casecode.pos.feature.sale"
}

dependencies {
    implementation(projects.core.data)

   /* testImplementation(libs.coroutines.test)
    testImplementation(libs.coroutines.android)*/
    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)
}