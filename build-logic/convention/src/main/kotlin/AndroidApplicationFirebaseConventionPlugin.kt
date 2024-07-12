import com.casecode.pos.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationFirebaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.gms.google-services")
                apply("com.google.firebase.firebase-perf")
               // apply("com.google.firebase.crashlytics")
            }

            dependencies {

                //  "implementation"(libs.findLibrary("firebase.messaging").get())
                // "implementation"(libs.findLibrary("firebase.analytics").get())
                //  "implementation"(libs.findLibrary("firebase.crashlytics").get())
            }

        /* extensions.configure<ApplicationExtension> {
            buildTypes.configureEach {
               // Disable the Crashlytics mapping file upload. This feature should only be
               // enabled if a Firebase backend is available and configured in
               // google-services.json.
         */
        /*    configure<CrashlyticsExtension> {
                     mappingFileUploadEnabled = false
                  } */
        /*
            }
         }*/
        }
    }
}