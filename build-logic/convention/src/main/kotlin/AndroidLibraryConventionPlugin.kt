import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import com.casecode.pos.Configuration
import com.casecode.pos.configureFlavors
import com.casecode.pos.configureGradleManagedDevices
import com.casecode.pos.configureKotlinAndroid
import com.casecode.pos.configurePrintApksTask
import com.casecode.pos.disableUnnecessaryAndroidTests
import com.casecode.pos.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin


class AndroidLibraryConventionPlugin : Plugin<Project>
{
   override fun apply(target: Project)
   {
      with(target) {
         with(pluginManager) {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.android")
            apply("pos.android.lint")
         }
         
         extensions.configure<LibraryExtension> {
            configureKotlinAndroid(this)
            defaultConfig.targetSdk = Configuration.CompileSdk
             defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

             testOptions.animationsDisabled = true
             configureFlavors(this)
             configureGradleManagedDevices(this)
             resourcePrefix = path.split("""\W""".toRegex()).drop(1).distinct().joinToString(separator = "_").lowercase() + "_"

         }
        
         extensions.configure<LibraryAndroidComponentsExtension> {
            configurePrintApksTask(this)
             disableUnnecessaryAndroidTests(target)
         }
         
         dependencies {
             add("implementation", libs.findLibrary("timber").get())
             add("androidTestImplementation", kotlin("test"))
             add("testImplementation", kotlin("test"))

         }
      }
   }
}