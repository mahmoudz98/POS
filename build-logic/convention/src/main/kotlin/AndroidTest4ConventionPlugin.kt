

import com.android.build.gradle.TestExtension
import com.casecode.pos.configureGradleManagedDevices
import com.casecode.pos.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidTest4ConventionPlugin : Plugin<Project>
{
   override fun apply(target: Project)
   {
      with(target) {
         with(pluginManager) {
            apply("com.android.test")
            apply("org.jetbrains.kotlin.android")
         }
         
         extensions.configure<TestExtension> {
            configureKotlinAndroid(this)
            defaultConfig.targetSdk = 34
            configureGradleManagedDevices(this)
         }
         
         dependencies {
            
         
         }
      }
    
      }
   
   
}
