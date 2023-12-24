
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
   alias(libs.plugins.hilt) apply false
   alias(libs.plugins.ksp) apply false
   
   alias(libs.plugins.gms.google.services) apply false
   alias(libs.plugins.kotlin.kapt) apply false
   alias(libs.plugins.android.junit5) apply false
   alias(libs.plugins.gradle.cache.fix) apply false
   
}


// TODo: use gradle profile https://developer.android.com/build/profile-your-build#getting_started
//TODO: Add Dependency graph let visualize dependencies in a graph.
//https://github.com/vanniktech/gradle-dependency-graph-generator-plugin
allprojects {
   // ...
   configurations.all {
      /*  resolutionStrategy.eachDependency {
         if (requested.group == "org.jetbrains.kotlin.android"){
            useVersion(libs.versions.kotlin.get())
         }
      } */
      resolutionStrategy {
         
         // fail eagerly on version conflict (includes transitive dependencies)
         // e.g. multiple different versions of the same dependency (group and name are equal)
        // failOnVersionConflict()
         // prefer modules that are part of this build (multi-project or composite build) over external modules
         //preferProjectModules()
         force(libs.fragment)
         //force("androidx.test.ext:junit:1.1.3")
         //force("androidx.test.espresso:espresso-core:3.4.0")
         
         // force certain versions of dependencies (including transitive)
        // force("androidx.core:core-ktx:1.12.0")
         
         //  cacheDynamicVersionsFor(10 * 60, "seconds")
         
      }
   }
}


tasks.register("clean",Delete::class){
   delete( rootProject.layout.buildDirectory)
}






