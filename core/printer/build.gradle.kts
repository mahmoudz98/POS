plugins {
    alias(libs.plugins.pos.android.library)
    alias(libs.plugins.pos.hilt)

}

android {
    namespace = "com.casecode.pos.core.printer"

}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)
    api (libs.escpos.thermalprinter.android)


}