
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.casecode.pos.configureSigning
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationSigningConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.getByType(ApplicationExtension::class.java)
            val components = extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
            configureSigning(extension, components)
        }
    }
}
