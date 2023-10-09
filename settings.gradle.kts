

pluginManagement {
   includeBuild("build-logic")
 /*   resolutionStrategy {
      eachPlugin {
         if (requested.id.namespace == "com.example")
         {
            useModule("com.example:sample-plugins:1.0.0")
         }
      }
   } */
   repositories {
      google()
      mavenCentral()
      gradlePluginPortal()
      mavenLocal()
   }
}

dependencyResolutionManagement {
   repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
   repositories {
      google()
      mavenCentral()
      gradlePluginPortal()
      maven("https://jitpack.io")
      
   }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "POS"
include (":app")
include (":domain")
include(":di")

include (":data")
include(":testing")

