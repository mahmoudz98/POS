import com.android.build.gradle.LibraryExtension
import com.casecode.pos.configureGradleManagedDevices
import com.casecode.pos.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("pos.android.library")
                apply("pos.hilt")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }
            extensions.configure<LibraryExtension> {
                testOptions.animationsDisabled = true
                configureGradleManagedDevices(this)
            }

            dependencies {
                "implementation"(project(":core:ui"))
                "implementation"(project(":core:designsystem"))
                "implementation"(libs.findLibrary("androidx.hilt.navigation.compose").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
                "implementation"(libs.findLibrary("androidx.navigation.compose").get())
                "implementation"(libs.findLibrary("androidx.tracing.ktx").get())
                "implementation"(libs.findLibrary("kotlinx.serialization.json").get())
                "implementation"(libs.findLibrary("kotlinx.collections.immutable").get())
                "testImplementation"(libs.findLibrary("androidx.navigation.testing").get())
                "androidTestImplementation"(
                    libs.findLibrary("androidx.lifecycle.runtimeTesting").get(),
                )
            }
        }
    }
}