import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.tasks.databinding.DataBindingGenBaseClassesTask
import com.casecode.pos.Configuration
import com.casecode.pos.configureBadgingTasks
import com.casecode.pos.configureGradleManagedDevices
import com.casecode.pos.configureKotlinAndroid
import com.casecode.pos.configurePrintApksTask
import com.casecode.pos.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool

class AndroidApplicationConventionPlugin : Plugin<Project>
{
   override fun apply(target: Project)
   {
   
      with(target) {
         with(pluginManager) {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
            apply("org.gradle.android.cache-fix")
              apply("pos.android.lint")
         }
      
         extensions.configure<ApplicationExtension> {
            configureKotlinAndroid(this)
            defaultConfig.targetSdk = Configuration.targetSdk
           configureGradleManagedDevices(this)
         }
         
         extensions.configure<ApplicationAndroidComponentsExtension> {
            configurePrintApksTask(this)
            configureBadgingTasks(extensions.getByType<BaseExtension>(), this)
      
            }
         }
 
      }
   
   
}