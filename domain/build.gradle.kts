plugins {
   alias(libs.plugins.pos.android.library)
   
}
//https://www.youtube.com/watch?v=YORvmxQBPeM
//https://proandroiddev.com/testing-github-actions-workflows-for-android-locally-with-docker-eb73b683dc34
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
   api(libs.firebase.firestore.ktx)
   api(libs.firebase.auth.ktx)
   
   testApi(libs.coroutines.test)
   
}
