plugins {
   alias(libs.plugins.pos.android.library)
   
}

android {
    namespace = "com.casecode.pos.domain"
 
   
    
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
    
    
    api(libs.firebase.firestore.ktx)
    
   
    testApi(projects.testing)
    //testApi(libs.test.hamcrest)
    //testApi(libs.test.hamcrest.library)
    
   
    //testApi(libs.test.mockk)
    
    testApi(libs.coroutines.test)
    
}
