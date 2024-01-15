package com.casecode.benchmark

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Rule
import org.junit.runner.RunWith

/**
 * Author: Mahmoud Abdalhafeez
 * Created: 1/7/2024
 * Description:
 */

@RunWith(AndroidJUnit4ClassRunner::class)
class BaselineProfileGenerator
{
   @RequiresApi(Build.VERSION_CODES.P)
   @get:Rule
   val rule = BaselineProfileRule()
   
   /*   @Test
     fun generate() = rule.collect(
        "com.casecode.pos.app"
                                  ){
        startActivityAndWait()
        device.wait(Until.hasObject(By.text()))
     } */
   
}