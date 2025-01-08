@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "POS"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":benchmarks")
include(":core:analytics")
include(":core:domain")
include(":core:model")
include(":core:common")
include(":core:data")
include(":core:firebase-services")
include(":core:datastore")
include(":core:datastore_proto")
include(":core:datastore-test")
include(":core:notifications")
include(":core:testing")
include(":core:designsystem")
include(":core:ui")
include(":core:printer")
include(":feature:bill")
include(":feature:employee")
include(":feature:inventory")
include(":feature:item")
include(":feature:reports")
include(":feature:purchase")
include(":feature:profile")
include(":feature:login-employee")
include(":feature:sale")
include(":feature:sales-report")
include(":feature:setting")
include(":feature:signin")
include(":feature:signout")
include(":feature:stepper")
include(":feature:supplier")
include(":lint")
include(":sync:work")
include(":ui-test-hilt-manifest")