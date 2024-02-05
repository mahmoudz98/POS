package com.casecode.pos

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroid(
     commonExtension: CommonExtension<*, *, *, *, *>,
                                           )
{
   commonExtension.apply {
      compileSdk = Configuration.compileSdk
      
      defaultConfig {
         minSdk = Configuration.minSdk
      }
      
      compileOptions {
         sourceCompatibility = JavaVersion.VERSION_17
         targetCompatibility = JavaVersion.VERSION_17
         isCoreLibraryDesugaringEnabled = true
      }
      
      
   }
   
   configureKotlin()
   
   dependencies {
      add("coreLibraryDesugaring", libs.findLibrary("android.desugarJdkLibs").get())
   }
}

/**
 * Configure base Kotlin options for JVM (non-Android)
 */
internal fun Project.configureKotlinJvm()
{
   extensions.configure<JavaPluginExtension> {
      
      sourceCompatibility = JavaVersion.VERSION_17
      targetCompatibility = JavaVersion.VERSION_17
   }
   configureKotlin()
}

/**
 * Configure base Kotlin options
 */
private fun Project.configureKotlin()
{
   tasks.withType<KotlinCompile>().configureEach {
      kotlinOptions {
         // Set JVM target to 17
         jvmTarget = JavaVersion.VERSION_17.toString()
         // Treat all Kotlin warnings as errors (disabled by default)
         // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
         val warningsAsErrors: String? by project
         allWarningsAsErrors = warningsAsErrors.toBoolean()
         freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            
            )
      }
   }
}
