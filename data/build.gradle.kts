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
}

dependencies {
   
   api(projects.domain)
   testApi(projects.domain)
   testApi(projects.testing)
  // testImplementation(projects.testing)
   
   
   //Coroutines
   implementation(libs.kotlinx.coroutines.services)
   api(libs.kotlinx.coroutines.android)
   
   api(libs.hilt.android)
 //  ksp(libs.hilt.compiler)
   
  // testApi(libs.junit.jupiter)
  // testRuntimeOnly(libs.junit.jupiter.engine)
   //testImplementation(libs.mockito.junit5)
   testImplementation(libs.test.mockk)
   // assertion test
   //testApi(libs.test.hamcrest)
   //testApi(libs.test.hamcrest.library)
   //testApi(libs.test.mockk)
   
  
   
   testApi(libs.coroutines.test)
   testApi(libs.hilt.android.testing)
   
   
   
   
}
