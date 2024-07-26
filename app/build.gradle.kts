import com.casecode.pos.PosBuildType

plugins {
    alias(libs.plugins.pos.android.application)
    alias(libs.plugins.pos.android.application.compose)
    alias(libs.plugins.pos.android.application.flavors)
    alias(libs.plugins.pos.android.firebase)
    alias(libs.plugins.pos.hilt)
}

android {

    defaultConfig {
        applicationId = "com.casecode.pos"
        versionCode = com.casecode.pos.Configuration.versionCode
        versionName = com.casecode.pos.Configuration.versionName

        resourceConfigurations.addAll(listOf("en", "ar"))
        testInstrumentationRunner = "com.casecode.pos.core.testing.PosTestRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = PosBuildType.DEBUG.applicationIdSuffix
        }
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = PosBuildType.RELEASE.applicationIdSuffix

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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


    namespace = "com.casecode.pos"
}

dependencies {
    implementation(projects.feature.loginEmployee)
    implementation(projects.feature.stepper)
    implementation(projects.feature.employee)
    implementation(projects.feature.invoice)
    implementation(projects.feature.item)
    implementation(projects.feature.profile)
    implementation(projects.feature.sale)
    implementation(projects.feature.setting)
    implementation(projects.feature.signout)
    implementation(projects.feature.reports)

    implementation(projects.core.ui)
    implementation(projects.core.designsystem)
    implementation(projects.core.data)

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



    debugCompileOnly(libs.kotlinx.coroutines.debug)
    // Debug tools
    // debugImplementation(libs.leakcanary)
    implementation(libs.timber)
    // ******* UNIT TESTING ******************************************************
    debugImplementation(projects.uiTestHiltManifest)
    testImplementation(projects.core.testing)

    kspTest(libs.hilt.compiler)

    // assertion
   // testImplementation(libs.test.hamcrest)
    //testImplementation(libs.test.hamcrest.library)

    // mockito with kotlin
    testImplementation(kotlin("test"))

    testImplementation(libs.coroutines.android)

    androidTestImplementation(projects.core.testing)
    androidTestImplementation(libs.firebase.testlab)
    androidTestImplementation(libs.coil.test)
    androidTestImplementation(libs.test.espresso.core)
    androidTestImplementation(libs.navigation.testing)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.hilt.android.testing)

    // AndroidX Test - Hilt testing
    kspAndroidTest(libs.hilt.compiler)

}
dependencyGuard {
    configuration("prodReleaseRuntimeClasspath")
}