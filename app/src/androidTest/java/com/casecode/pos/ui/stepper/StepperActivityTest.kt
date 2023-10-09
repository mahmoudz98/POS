package com.casecode.pos.ui.stepper

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.casecode.testing.BaseTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class StepperActivityTest:BaseTest()
{
   @get:Rule
   var hiltRule = HiltAndroidRule(this)
   
   
   override fun init()
   {
   
   }
   @Test
   fun testCompleted(){
   
   }
}