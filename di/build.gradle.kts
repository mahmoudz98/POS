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
   api(projects.data)
   api(projects.domain)
   api(libs.firebase.firestore.ktx)
   api(libs.firebase.auth.ktx)
   
  
   

}