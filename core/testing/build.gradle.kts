plugins {
    alias(libs.plugins.pos.android.library)
    alias(libs.plugins.pos.hilt)
    alias(libs.plugins.pos.android.firebase.library)

}

android {
    namespace = "com.casecode.pos.core.testing"

}

dependencies {

   // api(projects.core.data)
    api(projects.core.data)


    implementation (libs.googleid)

    api(libs.coroutines.test)
    api(libs.mockk.android){
        exclude(group = "org.junit.jupiter", module = "junit-jupiter")
    }
   // api(libs.mockk.agent)
    implementation(libs.test.runner)

    implementation(libs.hilt.android.testing)
   // implementation(libs.play.services.auth)
}