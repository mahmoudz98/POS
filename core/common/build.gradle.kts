
plugins {
    alias(libs.plugins.pos.android.library)
    alias(libs.plugins.pos.hilt)
}

android {
    namespace = "com.casecode.pos.core.common"
}

dependencies {
    implementation(libs.coroutines.core)
    testImplementation(libs.coroutines.test)
}