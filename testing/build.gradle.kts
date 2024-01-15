plugins {
   alias(libs.plugins.pos.android.library)
   alias(libs.plugins.pos.android.hilt)
   
}

android {
   namespace = "com.casecode.pos.testing"


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
   
   
   implementation(libs.hilt.android.testing)
   implementation(libs.test.espresso.idlingResource)
   
   
}