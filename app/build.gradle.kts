import com.android.build.gradle.internal.tasks.databinding.DataBindingGenBaseClassesTask
import com.casecode.pos.Configuration
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool

plugins {
   alias(libs.plugins.pos.android.application)
   alias(libs.plugins.pos.android.hilt)
   alias(libs.plugins.kotlin.kapt)
   alias(libs.plugins.pos.android.test)
   alias(libs.plugins.pos.android.firebase)
   
}
// Once the plugin is enabled, you can add JUnit 5 configuration options. Here, I set the test
// lifecycle to 'per_class'. By default, Junit 5 creates a new test class instance for each of your
// functions. This means that if you have 5 test cases inside a class, 5 instances are created in
// memory. By setting the lifecycle to per_class, only one test instance is created, and all of your
// test case functions run inside it with the same memory id. You can also annotate your classes
// on a class-by-class basis, but here, I'm enabling the lifecycle options globally.
/* junitPlatform {
   configurationParameter("junit.jupiter.testinstance.lifecycle.default", "per_class")
} */


android {
   
   
   defaultConfig {
      applicationId = "com.casecode.pos"
      versionCode = Configuration.versionCode
      versionName = Configuration.versionName
      
      resourceConfigurations.addAll(listOf("en", "ar"))
      
      // add the AndroidJUnitRunner, then connect JUnit 5 to the runner
      testInstrumentationRunner = "com.casecode.pos.PosTestRunner"
      testInstrumentationRunnerArguments["runnerBuilder"] =
         "de.mannodermaus.junit5.AndroidJUnit5Builder"
      
      testInstrumentationRunnerArguments["configurationParameters"] =
         "junit.jupiter.execution.parallel.enabled=true,junit.jupiter.execution.parallel.mode.default=concurrent"
      
   }
   
   
   buildTypes {
      debug {
         isPseudoLocalesEnabled = true
         
         //  isDebuggable = true
         isMinifyEnabled = false
         enableAndroidTestCoverage = true
         proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
         testProguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguardTest-rules.pro")
      }
      val release by getting {
         isMinifyEnabled = true
         
         proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro",
                      )
         
      }
      
   }
   
   
   /*     if (project.hasProperty("debug"))
      {
         splits.abi.isEnable = false
         splits.density.isEnable = false
         aaptOptions.cruncherEnabled = false
      } */
   
   
   
   
   packaging {
      resources {
         excludes.add("/META-INF/{AL2.0,LGPL2.1}")
         excludes.add("META-INF/NOTICE")
         excludes.add("META-INF/licenses/**")
         excludes.add("META-INF/DEPENDENCIES")
         excludes.add("META-INF/LICENSE")
         excludes.add("META-INF/NOTICE.txt")
         excludes.add("META-INF/LICENSE-notice.md")
         excludes.add("META-INF/DEPENDENCIES")
         excludes.add("MANIFEST.MF")
         excludes.add("build.xml")
      }
   }
   @Suppress("UnstableApiUsage")
   testOptions {
      
      animationsDisabled = true
      
      unitTests {
         isIncludeAndroidResources = true
         isReturnDefaultValues = true
         
         
         all { test ->
            test.useJUnitPlatform()
            with(test) {
               testLogging {
                  events = setOf(
                     org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                     org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                     org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                     org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
                     org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR,
                                )
               }
            }
         }
         
      }
      
      
   }
   
   /* junitPlatform {
       // Configure JUnit 5 tests here
       filters("debug") {
          excludeTags("slow")
       }
       
       // Using local dependency instead of Maven coordinates
       //   instrumentationTests.enabled = false
    }**/
   
   buildFeatures {
      
      dataBinding = true
      viewBinding = true
      buildConfig = true
   }
   lint {
      abortOnError = false
   }
   namespace = "com.casecode.pos"
}


androidComponents {
   
   onVariants(selector().all()) { variant ->
      afterEvaluate {
         val dataBindingTask =
            project.tasks.findByName("dataBindingGenBaseClasses" + variant.name.capitalized()) as? DataBindingGenBaseClassesTask
         if (dataBindingTask != null)
         {
            project.tasks.getByName("ksp" + variant.name.capitalized() + "Kotlin") {
               (this as AbstractKotlinCompileTool<*>).setSource(dataBindingTask.sourceOutFolder)
            }
         }
      }
   }
}


dependencies {
   
   implementation(projects.data)
   implementation(projects.domain)
   implementation(projects.di)
   implementation(projects.testing)
   
   testImplementation(projects.domain)
   testImplementation(projects.data)
   testImplementation(projects.testing)
   androidTestImplementation(projects.testing)
   
   // AndroidX
   implementation(libs.core)
   implementation(libs.activity)
   
   implementation(libs.appcompat)
   implementation(libs.lifecycle.viewmodel)
   implementation(libs.recyclerview)
   implementation(libs.slidingpanelayout)
   implementation(libs.window)
   androidTestImplementation(libs.window.testing)
   
   
   // UI tools
   implementation(libs.material)
   implementation(libs.android.stepper)
   implementation(libs.coil)
   
   testImplementation(libs.coil.test)
   
   testImplementation(libs.fragment.testing)
   
   
   
   implementation(libs.navigation.fragment)
   implementation(libs.navigation.ui)
   
   
   // coroutines
   implementation(libs.kotlinx.coroutines.android)
   
   debugCompileOnly(libs.kotlinx.coroutines.debug)
   
   
   // Debug tools
   // debugImplementation(libs.leakcanary)
   implementation(libs.timber)
   
   
   // ******* UNIT TESTING ******************************************************
   // use for testing live data
   testImplementation(libs.core.testing)
   
   // assertion
   testImplementation(libs.test.hamcrest)
   testImplementation(libs.test.hamcrest.library)
   
   // mockito with kotlin
   testImplementation(libs.test.mockk)
   
   // coroutines unit test
   testImplementation(libs.coroutines.test)
   testImplementation(libs.coroutines.android.test)
   
   
   // Once https://issuetracker.google.com/127986458 is fixed this can be testImplementation
   debugImplementation(libs.fragment.testing)
   implementation(libs.fragment.ktx)
   /*    implementation(libs.test.core)
      implementation(libs.test.ext.junit) */
   
   
   // ******* ANDROID TESTING ***************************************************
   implementation(libs.test.espresso.idlingResource)
   
   
   // Resolve conflicts between main and test APK:
   androidTestImplementation(libs.appcompat)
   androidTestImplementation(libs.material)
   androidTestImplementation(libs.androidx.annotation)
   
   
   androidTestImplementation(libs.test.core)
   androidTestImplementation(libs.test.ext.junit)
   androidTestImplementation(libs.test.ext.junit.ktx)
   androidTestImplementation(libs.test.core.ktx)
   androidTestImplementation(libs.test.monitor)
   androidTestImplementation(libs.test.orchestrator)
   androidTestImplementation(libs.test.rules)
   androidTestImplementation(libs.core.testing)
   
   
   
   androidTestImplementation(libs.test.hamcrest)
   androidTestImplementation(libs.test.hamcrest.library)
   
   androidTestImplementation(libs.mockk.android)
   
   androidTestImplementation(libs.navigation.testing)
   androidTestImplementation(libs.test.espresso.core)
   androidTestImplementation(libs.test.espresso.idlingResource)
   androidTestImplementation(libs.test.espresso.idling.concurrent)
   androidTestImplementation(libs.test.espresso.accessibility) {
      exclude(module = "protobuf-lite")
      
   }
   androidTestImplementation(libs.test.espresso.contrib) {
      exclude(module = "protobuf-lite")
      
   }
   
   
}