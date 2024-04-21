plugins {
    alias(libs.plugins.pos.android.library)
    // alias(libs.plugins.pos.android.firebase)
}
android {
    namespace = "com.casecode.pos.data"

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
    lint {
        abortOnError = false
    }
}

dependencies {

    api(projects.domain)
    implementation(libs.firebase.storage.ktx)
    testApi(projects.domain)
    testApi(projects.testing)

    // Coroutines
    implementation(libs.coroutines.services)
    api(libs.coroutines.android)
    api(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)
    // implementation(libs.firebase.performance)
    api(libs.hilt.android)
    implementation(libs.zxing.generate.barcode)

    testImplementation(libs.test.mockk)

    testApi(libs.coroutines.test)
    testApi(libs.hilt.android.testing)
}