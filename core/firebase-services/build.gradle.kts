plugins {
    alias(libs.plugins.pos.android.library)
    alias(libs.plugins.pos.hilt)
    alias(libs.plugins.pos.android.firebase.library)
    alias(libs.plugins.secrets)

}

android {
    namespace = "com.casecode.pos.core.firebase.services"
    buildFeatures {
        buildConfig = true
    }
}
secrets {
    defaultPropertiesFileName = "secrets.defaults.properties"
}
dependencies {
    implementation(projects.core.common)
    implementation(projects.core.datastore)

    implementation(libs.coroutines.android)
    implementation(libs.googleid)
}