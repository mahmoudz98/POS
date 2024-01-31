import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
   `kotlin-dsl`
}
group = "com.casecode.pos.buildlogic"


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
   compileOnly(libs.android.gradlePlugin)
   compileOnly(libs.android.tools.common)
   compileOnly(libs.firebase.crashlytics.gradlePlugin)
   compileOnly(libs.firebase.performance.gradlePlugin)
   compileOnly(libs.kotlin.gradlePlugin)
   compileOnly(libs.ksp.gradlePlugin)
   implementation(libs.truth)
}

tasks{
   validatePlugins{
      enableStricterValidation = false
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
   
      register("androidTest4") {
         id = "pos.android.test4"
         implementationClass = "AndroidTest4ConventionPlugin"
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