package com.casecode.pos

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Project
import java.io.File
import java.util.Properties

/**
 * Configure signing for the application.
 * Properties are read from environment variables or keystore.properties.
 */
internal fun Project.configureSigning(
    commonExtension: ApplicationExtension,
    androidComponents: ApplicationAndroidComponentsExtension,
) {
    commonExtension.apply {
        val keystorePropertiesFile = rootProject.file("keystore.properties")
        val keystoreProperties = Properties()

        if (keystorePropertiesFile.exists()) {
            keystorePropertiesFile.inputStream().use { input ->
                keystoreProperties.load(input)
            }
        }

        fun getSigningProperty(key: String, envKey: String): String? {
            return System.getenv(envKey) ?: keystoreProperties.getProperty(key)
        }

        signingConfigs {
            create("release") {
                val storeFilePath = getSigningProperty("RELEASE_STORE_FILE", "SIGNING_STORE_FILE")
                val storePasswordValue =
                    getSigningProperty("RELEASE_STORE_PASSWORD", "SIGNING_STORE_PASSWORD")
                val keyAliasValue = getSigningProperty("RELEASE_KEY_ALIAS", "SIGNING_KEY_ALIAS")
                val keyPasswordValue =
                    getSigningProperty("RELEASE_KEY_PASSWORD", "SIGNING_KEY_PASSWORD")

                if (storeFilePath != null && storePasswordValue != null && keyAliasValue != null && keyPasswordValue != null) {
                    val storeFileHandle = if (File(storeFilePath).isAbsolute) {
                        File(storeFilePath)
                    } else {
                        rootProject.file(storeFilePath)
                    }

                    if (storeFileHandle.exists()) {
                        storeFile = storeFileHandle
                        storePassword = storePasswordValue
                        keyAlias = keyAliasValue
                        keyPassword = keyPasswordValue
                    }
                }
            }

            // Dynamic per-flavor/build-type configs
            PosFlavor.entries.forEach { flavor ->
                PosBuildType.entries.forEach { buildType ->
                    if (buildType == PosBuildType.DEBUG) return@forEach

                    val flavorNameUpper = flavor.name.uppercase()
                    val buildTypeUpper = buildType.name.uppercase()

                    val storeFileKey = "${flavorNameUpper}_${buildTypeUpper}_STORE_FILE"
                    val storePasswordKey = "${flavorNameUpper}_${buildTypeUpper}_STORE_PASSWORD"
                    val keyAliasKey = "${flavorNameUpper}_${buildTypeUpper}_KEY_ALIAS"
                    val keyPasswordKey = "${flavorNameUpper}_${buildTypeUpper}_KEY_PASSWORD"

                    val storeFilePath = getSigningProperty(storeFileKey, storeFileKey)

                    if (storeFilePath != null) {
                        val variantName =
                            flavor.name + buildType.name.lowercase()
                                .replaceFirstChar { it.uppercase() }

                        create(variantName) {
                            val storeFileHandle = if (File(storeFilePath).isAbsolute) {
                                File(storeFilePath)
                            } else {
                                rootProject.file(storeFilePath)
                            }
                            if (storeFileHandle.exists()) {
                                storeFile = storeFileHandle
                                storePassword =
                                    getSigningProperty(storePasswordKey, storePasswordKey)
                                keyAlias = getSigningProperty(keyAliasKey, keyAliasKey)
                                keyPassword = getSigningProperty(keyPasswordKey, keyPasswordKey)
                            }
                        }
                    }
                }
            }
        }

        androidComponents.onVariants { variant ->
            val specificConfig = signingConfigs.findByName(variant.name)
            if (specificConfig != null) {
                variant.signingConfig.setConfig(specificConfig)
            } else if (variant.buildType == "release") {
                val releaseConfig = signingConfigs.findByName("release")
                if (releaseConfig != null) {
                    variant.signingConfig.setConfig(releaseConfig)
                }
            }
        }
    }
}
