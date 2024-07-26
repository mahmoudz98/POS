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
    implementation ("com.github.DantSu:ESCPOS-ThermalPrinter-Android:3.3.0")


}