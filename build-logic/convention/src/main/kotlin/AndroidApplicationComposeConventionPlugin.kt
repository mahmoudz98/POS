
import com.android.build.api.dsl.ApplicationExtension
import com.casecode.pos.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.kotlin.plugin.serialization")

            }
            configureAndroidCompose(the<ApplicationExtension>())
        }
    }
}
