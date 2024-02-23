plugins {
   alias(libs.plugins.pos.android.library)
   alias(libs.plugins.pos.android.hilt)
   
}

android {
   namespace = "com.casecode.pos.di"
}
dependencies {
   api(projects.data)
   api(projects.domain)
   api(libs.firebase.firestore.ktx)
   api(libs.firebase.auth.ktx)
   implementation(libs.play.services.auth)
    implementation(libs.firebase.storage.ktx)


}