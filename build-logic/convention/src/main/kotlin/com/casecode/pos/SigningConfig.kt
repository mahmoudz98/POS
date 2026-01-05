package com.casecode.pos

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Project
import java.io.File
import java.io.StringReader
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
        val keystoreProperties = Properties()
        val keystoreContent = providers.fileContents(
            project.isolated.rootProject.projectDirectory.file("keystore.properties"),
        ).asText

        if (keystoreContent.isPresent) {
            keystoreProperties.load(StringReader(keystoreContent.get()))
        }

        fun getSigningProperty(key: String, envKey: String): String? {
            return System.getenv(envKey) ?: keystoreProperties.getProperty(key)
        }

        signingConfigs {
            create("release") {
                val storeFilePath = getSigningProperty("RELEASE_STORE_FILE", "RELEASE_STORE_FILE")
                val storePasswordValue =
                    getSigningProperty("RELEASE_STORE_PASSWORD", "RELEASE_STORE_PASSWORD")
                val keyAliasValue = getSigningProperty("RELEASE_KEY_ALIAS", "RELEASE_KEY_ALIAS")
                val keyPasswordValue =
                    getSigningProperty("RELEASE_KEY_PASSWORD", "RELEASE_KEY_PASSWORD")

                if (storeFilePath != null && storePasswordValue != null && keyAliasValue != null && keyPasswordValue != null) {
                    val storeFileHandle = if (File(storeFilePath).isAbsolute) {
                        File(storeFilePath)
                    } else {
                        project.isolated.rootProject.projectDirectory.file(storeFilePath).asFile
                    }
                    
                    project.logger.lifecycle("POS: Configuring release signing. Path: ${storeFileHandle.absolutePath}, Exists: ${storeFileHandle.exists()}")

                    if (storeFileHandle.exists()) {
                        storeFile = storeFileHandle
                        storePassword = storePasswordValue
                        keyAlias = keyAliasValue
                        keyPassword = keyPasswordValue
                    }
                } else {
                    project.logger.lifecycle("POS: Release signing properties missing. Path: $storeFilePath")
                }
            }

            // Dynamic per-flavor/build-type configs
            PosFlavor.entries.forEach { flavor ->
                PosBuildType.entries.forEach { buildType ->

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
                                project.isolated.rootProject.projectDirectory.file(storeFilePath).asFile
                            }
                            
                            project.logger.lifecycle("POS: Configuring $variantName signing. Path: ${storeFileHandle.absolutePath}, Exists: ${storeFileHandle.exists()}")

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
            if (specificConfig != null && specificConfig.storeFile != null) {
                variant.signingConfig.setConfig(specificConfig)
            } else if (variant.buildType == "release") {
                val releaseConfig = signingConfigs.findByName("release")
                if (releaseConfig != null && releaseConfig.storeFile != null) {
                    variant.signingConfig.setConfig(releaseConfig)
                }
            }
        }
    }
}
