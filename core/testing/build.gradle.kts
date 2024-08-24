
plugins {
    alias(libs.plugins.pos.android.library)
    alias(libs.plugins.pos.hilt)
    alias(libs.plugins.pos.android.firebase.library)
}

android {
    namespace = "com.casecode.pos.core.testing"
}

dependencies {

    api(projects.core.data)

    // api(libs.test.hamcrest)
    //  compileOnly(libs.test.hamcrest.library)
    implementation(libs.googleid)

    api(libs.coroutines.test)
    api(libs.mockk.android) {
        exclude(group = "org.junit.jupiter", module = "junit-jupiter")
    }
    implementation(libs.mockk.agent)
    implementation(libs.androidx.test.runner)
    implementation(libs.androidx.test.rules)
    implementation(libs.hilt.android.testing)
    // implementation(libs.play.services.auth)
}