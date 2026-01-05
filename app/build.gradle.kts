/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.android.build.api.variant.BuildConfigField
import com.casecode.pos.Configuration
import com.casecode.pos.Configuration.APPLICATION_ID
import com.casecode.pos.PosBuildType
import java.io.StringReader
import java.util.Properties

plugins {
    alias(libs.plugins.pos.android.application)
    alias(libs.plugins.pos.android.application.compose)
    alias(libs.plugins.pos.android.application.flavors)
    alias(libs.plugins.pos.android.application.jacoco)
    alias(libs.plugins.pos.android.firebase)
    alias(libs.plugins.pos.hilt)
    alias(libs.plugins.google.osslicenses)
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.pos.android.application.signing)
}

android {
    defaultConfig {
        applicationId = APPLICATION_ID
        versionCode = Configuration.VERSION_CODE
        versionName = Configuration.VERSION_NAME

        testInstrumentationRunner = "$APPLICATION_ID.core.testing.PosTestRunner"
    }
    androidResources {
        localeFilters += listOf("en", "ar")
    }

    buildTypes {
        debug {
            applicationIdSuffix = PosBuildType.DEBUG.applicationIdSuffix
            vcsInfo {
                include = true
            }
        }
        release {
            isMinifyEnabled = providers.gradleProperty("minifyWithR8")
                .map(String::toBooleanStrict).getOrElse(true)
            applicationIdSuffix = PosBuildType.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.named("release").get()
            baselineProfile.automaticGenerationDuringBuild = true
        }
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    buildFeatures {
        buildConfig = true
    }
    testOptions.unitTests.isIncludeAndroidResources = true
    namespace = APPLICATION_ID
}
/*androidComponents {
    onVariants { variant ->
        val name = variant.name.lowercase()

        val shouldUseDebugSigning =
            name.contains("benchmark") ||
                    name.contains("nonminified") ||
                    name.contains("demo")

        if (shouldUseDebugSigning) {
            variant.signingConfig?.setConfig(
                android.signingConfigs.getByName("debug")
            )
        }
    }
}*/
dependencies {
    implementation(projects.feature.login)
    implementation(projects.feature.loginEmployee)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.employee)
    implementation(projects.feature.salesReport)
    implementation(projects.feature.inventory)
    implementation(projects.feature.item)
    implementation(projects.feature.purchase)
    implementation(projects.feature.supplier)
    implementation(projects.feature.bill)
    implementation(projects.feature.profile)
    implementation(projects.feature.sale)
    implementation(projects.feature.setting)
    implementation(projects.feature.signout)
    implementation(projects.feature.reports)

    implementation(projects.core.ui)
    implementation(projects.core.designsystem)
    implementation(projects.core.data)
    implementation(projects.sync.work)
    // AndroidX
    implementation(libs.androidx.activity.compose)
    implementation(libs.appcompat)
    implementation(libs.core.splashscreen)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)

    ksp(libs.hilt.compiler)
    implementation(libs.revenuecat.purchases)

    implementation(libs.timber)

    debugCompileOnly(libs.kotlinx.coroutines.debug)
    debugImplementation(projects.uiTestHiltManifest)
    testImplementation(projects.core.testing)
    testImplementation(libs.kotlin.test)

    kspTest(libs.hilt.compiler)
    testImplementation(libs.coroutines.android)

    androidTestImplementation(projects.core.testing)
    kspAndroidTest(libs.hilt.compiler)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.firebase.testlab)
    androidTestImplementation(libs.coil.test)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.kotlin.test)

    baselineProfile(projects.benchmarks)
}
baselineProfile {
    // Don't build on every iteration of a full assemble.
    // Instead enable generation directly for the release build variant.
    automaticGenerationDuringBuild = false
    dexLayoutOptimization = true
}

dependencyGuard {
    configuration("prodReleaseRuntimeClasspath")
}
val revenuecatId = providers.fileContents(
    isolated.rootProject.projectDirectory.file("local.properties"),
).asText.map { text ->
    val properties = Properties()
    properties.load(StringReader(text))
    properties["revenuecat_id"]
}.orElse("")

androidComponents {
    onVariants {
        it.buildConfigFields!!.put(
            "revenuecat_id",
            revenuecatId.map { value ->
                BuildConfigField(type = "String", value = """"$value"""", comment = null)
            },
        )
    }
}
