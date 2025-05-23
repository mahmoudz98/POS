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

import com.casecode.pos.PosBuildType
import com.casecode.pos.Configuration
import com.casecode.pos.Configuration.APPLICATION_ID

plugins {
    alias(libs.plugins.pos.android.application)
    alias(libs.plugins.pos.android.application.compose)
    alias(libs.plugins.pos.android.application.flavors)
    alias(libs.plugins.pos.android.application.jacoco)
    alias(libs.plugins.pos.android.firebase)
    alias(libs.plugins.pos.hilt)
    alias(libs.plugins.baselineprofile)
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
            isMinifyEnabled = true
            applicationIdSuffix = PosBuildType.RELEASE.applicationIdSuffix
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.named("debug").get()
            baselineProfile.automaticGenerationDuringBuild = true
        }
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    namespace = APPLICATION_ID
}

dependencies {
    implementation(projects.feature.signin)
    implementation(projects.feature.stepper)
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

    debugCompileOnly(libs.kotlinx.coroutines.debug)
    // debugImplementation(libs.leakcanary)
    implementation(libs.timber)
    // ******* UNIT TESTING ******************************************************
    debugImplementation(projects.uiTestHiltManifest)
    testImplementation(projects.core.testing)

    kspTest(libs.hilt.compiler)
    testImplementation(kotlin("test"))
    testImplementation(libs.coroutines.android)

    androidTestImplementation(projects.core.testing)
    androidTestImplementation(libs.firebase.testlab)
    androidTestImplementation(libs.coil.test)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.hilt.android.testing)

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