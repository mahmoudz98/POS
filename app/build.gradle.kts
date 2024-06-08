import com.casecode.pos.PosBuildType

plugins {
    alias(libs.plugins.pos.android.application)
    alias(libs.plugins.pos.android.application.compose)
    alias(libs.plugins.pos.android.hilt)
    alias(libs.plugins.pos.android.firebase)
    alias(libs.plugins.navigation.safeargs.kotlin)
    alias(libs.plugins.kotlin.android)
}

android {

    defaultConfig {
        applicationId = "com.casecode.pos"
        versionCode = com.casecode.pos.Configuration.versionCode
        versionName = com.casecode.pos.Configuration.versionName

        resourceConfigurations.addAll(listOf("en", "ar"))
        testInstrumentationRunner = "com.casecode.testing.PosTestRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = PosBuildType.DEBUG.applicationIdSuffix

        }
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = PosBuildType.RELEASE.applicationIdSuffix

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    @Suppress("UnstableApiUsage") testOptions {
        animationsDisabled = true
        packaging {
            jniLibs {
                useLegacyPackaging = true
            }
        }
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }

    namespace = "com.casecode.pos"
}

/*androidComponents {
    onVariants(selector().all()) { variant ->
        afterEvaluate {
            val dataBindingTask =
                project.tasks.findByName("dataBindingGenBaseClasses" + variant.name.capitalized()) as? DataBindingGenBaseClassesTask
            if (dataBindingTask != null) {
                project.tasks.getByName("ksp" + variant.name.capitalized() + "Kotlin") {
                    (this as AbstractKotlinCompileTool<*>).setSource(dataBindingTask.sourceOutFolder)
                }
            }
        }
    }
}*/

dependencies {

    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.di)

    // AndroidX
    implementation(libs.androidx.activity.compose)
    implementation(libs.core)
    implementation(libs.appcompat)
    implementation(libs.core.splashscreen)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)

    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.navigationSuite)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.livedata.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.accompanist.permissions )

    implementation(libs.coil)
    implementation(libs.coil.kt.compose)


    testImplementation(projects.domain)
    testImplementation(projects.data)
    testImplementation(projects.di)
    testImplementation(projects.testing)

    androidTestImplementation(projects.domain)
    androidTestImplementation(projects.testing)
    androidTestImplementation(libs.test.espresso.core)
    androidTestImplementation(libs.navigation.testing)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.hilt.android.testing)


//    implementation(libs.activity)
    //implementation(libs.fragment.ktx)
   /* implementation(libs.appcompat.resources)
    implementation(libs.recyclerview)
    implementation(libs.slidingpanelayout)
    implementation(libs.window)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.runtime)
    // UI tools
    implementation(libs.material)
    implementation(libs.android.stepper)

    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)*/

    // scanner barcode
    implementation(libs.zxing.android.embedded)

    // coroutines
    implementation(libs.coroutines.android)
    debugCompileOnly(libs.kotlinx.coroutines.debug)
    // Debug tools
    // debugImplementation(libs.leakcanary)
    implementation(libs.timber)
    // ******* UNIT TESTING ******************************************************
    // use for testing live data
    testImplementation(libs.core.testing)

    // jvm test - Hilt
    kspTest(libs.hilt.compiler)

    // assertion
    testImplementation(libs.test.hamcrest)
    testImplementation(libs.test.hamcrest.library)

    // mockito with kotlin
    testImplementation(libs.test.mockk)
    testImplementation(kotlin("test"))
    // coroutines unit test
    testImplementation(libs.coroutines.test)
    testImplementation(libs.coroutines.android)
    // Once https://issuetracker.google.com/127986458 is fixed this can be testImplementation
    //debugImplementation(libs.fragment.testing)
    /*    implementation(libs.test.core)
       implementation(libs.test.ext.junit) */
    // ******* ANDROID TESTING ***************************************************
    //implementation(libs.test.espresso.idlingResource)

   // androidTestImplementation(libs.window.testing)

    // Resolve conflicts between main and test APK:
    // androidTestImplementation(libs.appcompat)
    //androidTestImplementation(libs.material)
    //  androidTestImplementation(libs.androidx.annotation)
    androidTestImplementation(libs.firebase.testlab)

   /* androidTestImplementation(libs.test.core)
    androidTestImplementation(libs.test.core.ktx)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.test.ext.junit.ktx)
    androidTestImplementation(libs.test.monitor)
    androidTestImplementation(libs.test.orchestrator)
    androidTestImplementation(libs.test.rules)
    androidTestImplementation(libs.core.testing)*/

    androidTestImplementation(libs.test.hamcrest)
    androidTestImplementation(libs.test.hamcrest.library)

    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.mockk.agent)
    // androidTestImplementation(libs.test.mockk)

    androidTestImplementation(libs.coil.test)
    //  testImplementation(libs.fragment.testing)

   /* androidTestImplementation(libs.test.espresso.idlingResource)
    androidTestImplementation(libs.test.espresso.idling.concurrent)
    androidTestImplementation(libs.test.espresso.accessibility) {
        exclude(module = "protobuf-lite")
    }
    androidTestImplementation(libs.test.espresso.contrib) {
        exclude(module = "protobuf-lite")
    }*/

    // AndroidX Test - Hilt testing
    kspAndroidTest(libs.hilt.compiler)

    // implementation(kotlin("reflect"))
    //  androidTestImplementation(kotlin("reflect"))
}

tasks.withType<Test>().configureEach {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
}