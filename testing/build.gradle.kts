@Suppress("DSL_SCOPE_VIOLATION")
plugins {
   alias(libs.plugins.pos.android.library)
   
   /* alias(libs.plugins.android.library)
   alias(libs.plugins.kotlin.android) */
   
}

android {
   namespace = "com.casecode.pos.testing"
   
   lint {
      abortOnError = false
   }
}

dependencies {
   
   api(projects.domain)
   api(projects.data)
   api(projects.di)
   
   // use for testing live data
   api(libs.core.testing)
   
   api(libs.coroutines.test)
   api(libs.test.mockk)
   api(libs.test.runner)
   api(libs.junit.jupiter.api)
   api(libs.hilt.android.testing)
   
   
}