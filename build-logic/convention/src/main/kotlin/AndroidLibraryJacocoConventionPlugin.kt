import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.casecode.pos.configureJacoco
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.the

class AndroidLibraryJacocoConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("jacoco")

            the<LibraryExtension>().buildTypes.configureEach {
                enableAndroidTestCoverage = true
                enableUnitTestCoverage = true
            }

            configureJacoco(the<LibraryAndroidComponentsExtension>())
        }
    }
}
