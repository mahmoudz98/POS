plugins {
    alias(libs.plugins.pos.android.library)
    alias(libs.plugins.pos.hilt)
    alias(libs.plugins.pos.android.firebase.library)
    alias(libs.plugins.secrets)
}
android {
    namespace = "com.casecode.pos.core.data"

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
    buildFeatures {
        buildConfig = true
    }
}
secrets {
    defaultPropertiesFileName = "secrets.defaults.properties"
}
dependencies {

    api(projects.core.domain)
    api(projects.core.common)
    api(projects.core.datastore)

    // Coroutines
    implementation(libs.coroutines.android)

    implementation(libs.googleid)
    implementation(libs.billing.ktx)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)

    implementation(libs.hilt.android)
    implementation(libs.zxing.generate.barcode)

    testImplementation(libs.test.hamcrest)
    testCompileOnly(libs.test.hamcrest.library)
    testImplementation(projects.core.testing)
    testImplementation(libs.coroutines.test)
    implementation(libs.mockk.agent)

}