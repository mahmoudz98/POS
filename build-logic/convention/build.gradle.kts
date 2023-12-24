import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
   `kotlin-dsl`
   
}

group = "com.casecode.pos.buildlogic"


// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
   toolchain {
      languageVersion.set(JavaLanguageVersion.of(17))
   }
}

tasks.withType<KotlinCompile>().configureEach {
   kotlinOptions {
      jvmTarget = JavaVersion.VERSION_17.toString()
   }
}

dependencies {
   //compileOnly("org.gradle.android.cache-fix:org.gradle.android.cache-fix.gradle.plugin:3.0")
   compileOnly(libs.android.gradlePlugin)
   compileOnly(libs.android.tools.common)
   compileOnly(libs.firebase.crashlytics.gradlePlugin)
   compileOnly(libs.firebase.performance.gradlePlugin)
   compileOnly(libs.kotlin.gradlePlugin)
   compileOnly(libs.ksp.gradlePlugin)
   testCompileOnly(libs.android.junit5.plugin)
}

tasks{
   validatePlugins{
      enableStricterValidation = true
      failOnWarning = true
   }
}

gradlePlugin {
   /**
    * Register convention plugins so they are available in the build scripts of the application
    */
   plugins {
      register("androidApplication") {
         id = "pos.android.application"
         implementationClass = "AndroidApplicationConventionPlugin"
      }
      register("androidLibrary") {
         id = "pos.android.library"
         implementationClass = "AndroidLibraryConventionPlugin"
         
      }
      register("androidHilt") {
         id = "pos.android.hilt"
         implementationClass = "AndroidHiltConventionPlugin"
         
      }
      register("androidFirebase") {
         id = "pos.android.firebase"
         implementationClass = "AndroidApplicationFirebaseConventionPlugin"
         
      }
      register("androidTest") {
         id = "pos.android.test"
         implementationClass = "AndroidTestConventionPlugin"
      }
      
      register("androidLint") {
         id = "pos.android.lint"
         implementationClass = "AndroidLintConventionPlugin"
      }
      
      register("jvmLibrary") {
         id = "pos.jvm.library"
         implementationClass = "JvmLibraryConventionPlugin"
      }
   }
}