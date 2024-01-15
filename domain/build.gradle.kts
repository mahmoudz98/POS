plugins {
    alias(libs.plugins.pos.android.library)
}

android {
    namespace = "com.casecode.pos.domain"

    testOptions {
        unitTests {
            this.all {
                it.useJUnitPlatform()
            }
            isReturnDefaultValues = true
        }
    }

}

dependencies {
    // firebase
    api(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth.ktx)

    testRuntimeOnly(libs.junit.jupiter.engine)
    testApi(libs.test.hamcrest)
    testApi(libs.test.hamcrest.library)
    testApi(libs.coroutines.test)
}
