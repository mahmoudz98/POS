
buildscript {
   repositories {
      google()
      mavenCentral()
   }
}

plugins {
   //  base plugins
   alias(libs.plugins.android.application) apply false
   alias(libs.plugins.android.test) apply false
   
   alias(libs.plugins.kotlin.jvm) apply false
   alias(libs.plugins.kotlin.serialization) apply false
   alias(libs.plugins.firebase.crashlytics) apply false
   alias(libs.plugins.firebase.perf) apply false
   alias(libs.plugins.kotlin.kapt) apply false
   alias(libs.plugins.hilt) apply false
   alias(libs.plugins.ksp) apply false
   
   alias(libs.plugins.gms.google.services) apply false
   alias(libs.plugins.kotlin.android) apply false
   alias(libs.plugins.gradle.cache.fix) apply false
   
   
}



// TODo: use gradle profile https://developer.android.com/build/profile-your-build#getting_started
//TODO: Add Dependency graph let visualize dependencies in a graph.








