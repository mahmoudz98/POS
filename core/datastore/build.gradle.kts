plugins {
    alias(libs.plugins.pos.android.library)
    alias(libs.plugins.pos.hilt)


}

android {
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
    namespace = "com.casecode.pos.core.datastore"
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}
dependencies {
    api(projects.core.model)
    implementation(libs.androidx.dataStore.core)
    implementation(projects.core.datastoreProto)

    implementation(projects.core.common)

    testImplementation(projects.core.datastoreTest)
    testImplementation(libs.coroutines.test)
}