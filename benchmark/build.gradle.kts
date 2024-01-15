plugins {
   alias(libs.plugins.android.test)
   alias(libs.plugins.pos.android.test4)
}

android {
   namespace = "com.casecode.pos.benchmark"
   
   defaultConfig {
      minSdk = 27
      
      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
   }
   buildFeatures {
      buildConfig = true
   }
   buildTypes {
      // This benchmark buildType is used for benchmarking, and should function like your
      // release build (for example, with minification on). It"s signed with a debug key
      // for easy local/CI testing.
      create("benchmark") {
         isDebuggable = true
         signingConfig = getByName("debug").signingConfig
         matchingFallbacks += listOf("release")
      }
   }
   
   targetProjectPath = ":app"
   experimentalProperties["android.experimental.self-instrumenting"] = true
}

dependencies {
   implementation(libs.androidx.benchmark.macro.junit4)
   
   implementation(libs.test.core)
   implementation(libs.test.espresso.core)
   implementation(libs.test.ext.junit)
   implementation(libs.test.rules)
   implementation(libs.test.runner)
   implementation(libs.androidx.uiautomator)
}

androidComponents {
   beforeVariants(selector().all()) {
      it.enable = it.buildType == "benchmark"
   }
}