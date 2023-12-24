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
  
   /*  lint {
       abortOnError = false
    } */
   
}

dependencies {
   
   
   api(libs.firebase.firestore.ktx)
   
  // testApi(libs.junit.jupiter)
   testRuntimeOnly(libs.junit.jupiter.engine)
   testApi(libs.test.hamcrest)
   testApi(libs.test.hamcrest.library)
   
  // testImplementation(projects.testing)
   
   
   //testApi(libs.test.mockk)
   
   testApi(libs.coroutines.test)
   
}
