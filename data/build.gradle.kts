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
   
   api(projects.domain)
   testApi(projects.domain)
   testApi(projects.testing)
   
   //Coroutines
   implementation(libs.kotlinx.coroutines.services)
   api(libs.kotlinx.coroutines.android)
   api(libs.firebase.auth.ktx)
   api(libs.hilt.android)

   testImplementation(libs.test.mockk)
   
   testApi(libs.coroutines.test)
   testApi(libs.hilt.android.testing)
   
   
   
   
}
