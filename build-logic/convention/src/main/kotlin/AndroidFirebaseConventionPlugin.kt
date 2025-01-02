import com.casecode.pos.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude

class AndroidFirebaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                val bom = libs.findLibrary("firebase-bom").get()
                add("implementation", platform(bom))
                "implementation"(libs.findLibrary("play.services.auth").get())
                "api"(libs.findLibrary("firebase.auth").get())
                "api"(libs.findLibrary("firebase.firestore").get())
                "api"(libs.findLibrary("firebase.storage").get())
                "implementation"(libs.findLibrary("firebase.analytics").get())
                "implementation"(libs.findLibrary("firebase.crashlytics").get())
                "implementation"(libs.findLibrary("firebase.performance").get()) {
                    exclude(group = "com.google.protobuf", module = "protobuf-javalite")
                    exclude(group = "com.google.firebase", module = "protolite-well-known-types")
                }
            }
        }
    }
}