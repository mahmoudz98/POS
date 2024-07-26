plugins {
    alias(libs.plugins.pos.android.library)
    alias(libs.plugins.pos.hilt)
}

android {
    namespace = "com.casecode.pos.core.datastore.test"

}

dependencies {

    implementation(libs.hilt.android.testing)
    implementation(projects.core.common)
    implementation(projects.core.datastore)
    implementation(libs.androidx.dataStore.core)
    implementation(projects.core.datastoreProto)
}