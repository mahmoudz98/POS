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
   
   packaging {
      
      resources {
         excludes.add("/META-INF/{AL2.0,LGPL2.1}")
         excludes.add("/META-INF/NOTICE.md")
         excludes.add ("/META-INF/licenses/**")
         excludes.add ("META-INF/LICENSE.md")
         excludes.add ("META-INF/LICENSE-notice.md")
         excludes.add("META-INF/DEPENDENCIES")
         excludes.add("DebugProbesKt.bin")
      }
   }
   
}

dependencies {
   
   testApi(projects.testing)
   api(libs.firebase.firestore.ktx)
   api(libs.firebase.auth.ktx)
   
   testApi(libs.coroutines.test)
   
}
