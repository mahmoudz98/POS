plugins {
    alias(libs.plugins.pos.android.library)
    alias(libs.plugins.pos.android.hilt)
}

android {
    namespace = "com.casecode.pos.testing"

/*    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
            excludes.add("/META-INF/NOTICE.md")
            excludes.add("/META-INF/licenses/**")
            excludes.add("META-INF/LICENSE.md")
            excludes.add("META-INF/LICENSE-notice.md")
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("DebugProbesKt.bin")
        }
    }*/

 */
}

dependencies {

    api(projects.domain)
    api(projects.data)
    api(projects.di)

    // use for testing live data
/*    api(platform(libs.firebase.bom))
    api(libs.firebase.auth)
    api(libs.firebase.storage)*/

    implementation(libs.core.testing)
    implementation(libs.coroutines.test)
    implementation(libs.test.mockk)
    implementation(libs.test.runner)

    implementation(libs.hilt.android.testing)
    implementation(libs.play.services.auth)
}