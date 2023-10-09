import com.casecode.pos.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidTestConventionPlugin : Plugin<Project>
{
   override fun apply(target: Project)
   {
      with(target) {
         with(pluginManager) {
            apply("org.jetbrains.kotlin.android")
            apply("de.mannodermaus.android-junit5")
            //   apply("org.jetbrains.kotlinx.kover")
         }
         
       
         
         dependencies {
            add("testImplementation", project(":testing"))
            add( "testImplementation", libs.findLibrary("junit.jupiter").get())
            add( "testImplementation", libs.findLibrary("junit.jupiter.params").get())
                 add("testRuntimeOnly", libs.findLibrary("junit.jupiter.engine").get())
                 add("testRuntimeOnly", libs.findLibrary("junit.vintage.engine").get())
            
            // The instrumentation test companion libraries
            add("androidTestImplementation", project(":testing"))
            add("androidTestImplementation", libs.findLibrary("junit.jupiter.api").get())
            add("androidTestImplementation", libs.findLibrary("junit.jupiter.params").get())
          add(  "androidTestImplementation" ,libs.findLibrary("junit5.android.core").get())
           add( "androidTestRuntimeOnly",libs.findLibrary("junit5.android.runner").get())
            // For permission
            add("androidTestImplementation",libs.findLibrary("junit5.test.ext").get())
         }
      }
   }
   
}