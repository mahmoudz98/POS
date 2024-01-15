plugins {
    alias(libs.plugins.pos.android.library)
}

android {
    namespace = "com.casecode.pos.data"

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests {
            isReturnDefaultValues = true
            this.all {
                it.useJUnitPlatform()
            }
        }
    }

}

dependencies {
    implementation(projects.domain)
    testImplementation(projects.domain)

    // coroutines
    api(libs.kotlinx.coroutines.android)
    // hilt
    api(libs.hilt.android)
    // firebase-auth
    implementation(libs.firebase.auth.ktx)

    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.mockito.junit5)
    testImplementation(libs.test.mockk)
    testApi(libs.test.hamcrest)
    testApi(libs.test.hamcrest.library)
    testApi(libs.coroutines.test)
    testApi(libs.hilt.android.testing)
}
