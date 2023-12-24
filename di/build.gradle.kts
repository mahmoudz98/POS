plugins {
   alias(libs.plugins.pos.android.library)
   alias(libs.plugins.pos.android.hilt)
   
}

android {
   namespace = "com.casecode.pos.di"
   
   
   hilt {
      enableAggregatingTask = true
   }
   lint {
      abortOnError = true
   }
   
}
dependencies {
   implementation(projects.data)
   implementation(projects.domain)
   implementation(libs.firebase.firestore.ktx)
   implementation(libs.firebase.auth.ktx)

   
   
   
}