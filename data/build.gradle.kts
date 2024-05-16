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
    testApi(projects.domain)
    testApi(projects.testing)

    // Coroutines
    implementation(libs.coroutines.services)
    api(libs.coroutines.android)

    api(platform(libs.firebase.bom))
    api(libs.firebase.auth)
    api(libs.firebase.storage)
    api(libs.firebase.performance)
    api(libs.play.services.auth)
   // implementation (libs.androidx.credentials)
    //implementation (libs.androidx.credentials.play.services.auth)
    api (libs.googleid)

    // hi
    api(libs.hilt.android)
    implementation(libs.zxing.generate.barcode)

    testImplementation(libs.test.mockk)

    testApi(libs.coroutines.test)
    testApi(libs.hilt.android.testing)
}