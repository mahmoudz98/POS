plugins {
    alias(libs.plugins.pos.android.library)
    alias(libs.plugins.pos.android.hilt)
    alias(libs.plugins.secrets)
}
android {
    buildFeatures {
        buildConfig = true
    }
    lint {
        abortOnError = false
    }
    namespace = "com.casecode.pos.di"
}
secrets {
    defaultPropertiesFileName = "secrets.defaults.properties"
}
dependencies {
    api(projects.data)
    api(projects.domain)

    /*api(platform(libs.firebase.bom))

    api(libs.firebase.auth)
    api(libs.firebase.storage)
    api(libs.play.services.auth)
    api(libs.googleid)*/

}