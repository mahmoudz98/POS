
package com.casecode.pos

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ManagedVirtualDevice
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke

/**
 * Configure project for Gradle managed devices
 */
internal fun configureGradleManagedDevices(
     commonExtension: CommonExtension<*, *, *, *, *>,
                                          )
{
   val samsungJ7 = DeviceConfig("samsung SM-G610F", 27, "armeabi-v7a")
   val resizable = DeviceConfig("Resizable (Experimental) API 33", 33, "x86_64")
   //val pixel4 = DeviceConfig("Pixel 4", 30, "aosp-atd")
   //val pixel6 = DeviceConfig("Pixel 6", 31, "aosp")
   val pixelC = DeviceConfig("Pixel C", 30, "aosp-atd")
   
   val allDevices = listOf(samsungJ7, resizable, pixelC)
   val ciDevices = listOf(samsungJ7, resizable, pixelC)
   
   commonExtension.testOptions {
      managedDevices {
         devices {
            allDevices.forEach { deviceConfig ->
               maybeCreate(deviceConfig.taskName, ManagedVirtualDevice::class.java).apply {
                  device = deviceConfig.device
                  apiLevel = deviceConfig.apiLevel
                  systemImageSource = deviceConfig.systemImageSource
               }
            }
         }
         groups {
            maybeCreate("ci").apply {
               ciDevices.forEach { deviceConfig ->
                  targetDevices.add(devices[deviceConfig.taskName])
               }
            }
         }
      }
   }
}

private data class DeviceConfig(
     val device: String,
     val apiLevel: Int,
     val systemImageSource: String,
                               )
{
   val taskName = buildString {
      append(device.lowercase().replace(" ", ""))
      append("api")
      append(apiLevel.toString())
      append(systemImageSource.replace("-", ""))
   }
}
