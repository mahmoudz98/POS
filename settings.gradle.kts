@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "POS"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":benchmark")
include(":lint")
include(":core:analytics")
include(":core:domain")
include(":core:model")
include(":core:common")
include(":core:data")
include(":core:firebase-services")
include(":core:datastore")
include(":core:datastore_proto")
include(":core:datastore-test")
include(":core:testing")
include(":core:designsystem")
include(":core:ui")
include(":core:printer")
include(":feature:signin")
include(":feature:login-employee")
include(":feature:stepper")
include(":feature:employee")
include(":feature:sales-report")
include(":feature:inventory")
include(":feature:item")
include(":feature:purchase")
include(":feature:supplier")
include(":feature:bills")
include(":feature:profile")
include(":feature:sale")
include(":feature:setting")
include(":feature:signout")
include(":feature:reports")
include(":ui-test-hilt-manifest")