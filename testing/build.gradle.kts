plugins {
   alias(libs.plugins.pos.android.library)
}

android {
   namespace = "com.casecode.pos.testing"
   packaging {
      resources {
         excludes.add("/META-INF/{AL2.0,LGPL2.1}")
         excludes.add("META-INF/LICENSE-notice.md")
         
         excludes.add("META-INF/LICENSE.md")
         excludes.add("META-INF/NOTICE")
         excludes.add("META-INF/licenses/**")
         excludes.add("META-INF/DEPENDENCIES")
         excludes.add("META-INF/LICENSE")
         excludes.add("META-INF/NOTICE.txt")
         excludes.add("META-INF/DEPENDENCIES")
         excludes.add("MANIFEST.MF")
         //excludes.add("build.xml")
      }
   }
  /*  lint {
      abortOnError = false
   } */
}

dependencies {
   
   implementation(projects.domain)
   implementation(projects.data)
   implementation(projects.di)
   
   // use for testing live data
   api(libs.core.testing)
   
   api(libs.coroutines.test)
   api(libs.test.mockk)
   api(libs.test.runner)
   implementation(libs.junit.jupiter.api)
   
   
   api(libs.hilt.android.testing)
   api(libs.test.espresso.idlingResource)
   
   
}