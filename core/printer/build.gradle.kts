plugins {
    alias(libs.plugins.pos.android.library)
    alias(libs.plugins.pos.hilt)
}

android {
    namespace = "com.casecode.pos.core.printer"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.common)
    api(libs.escpos.thermalprinter.android)

    testImplementation(projects.core.testing)
}