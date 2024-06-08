plugins {
    alias(libs.plugins.pos.android.library)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.casecode.pos.domain"

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {

    testApi(projects.testing)

    api(platform(libs.firebase.bom))
    api(libs.firebase.auth)
    api(libs.firebase.firestore)

    testApi(libs.coroutines.test)
}