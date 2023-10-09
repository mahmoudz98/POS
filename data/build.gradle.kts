plugins {
   
   alias(libs.plugins.pos.android.library)

   
}

android {
   namespace = "com.casecode.pos.data"
   
  
   @Suppress("UnstableApiUsage")
   testOptions {
      unitTests {
         isReturnDefaultValues = true
      }
   }
   
   lint {
      abortOnError = false
   }


}

dependencies {
   
  
   
   api(projects.domain)
   testApi(projects.domain)
   testApi(projects.testing)
   
   
   //Coroutines
   api(libs.kotlinx.coroutines.android)
   
   api(libs.hilt.android)
 //  ksp(libs.hilt.compiler)
   
   
   // assertion test
   //testApi(libs.test.hamcrest)
 //  testApi(libs.test.hamcrest.library)
   //testApi(libs.test.mockk)
   
  
   
   testApi(libs.coroutines.test)
   testApi(libs.hilt.android.testing)
   
   
   
   
}
