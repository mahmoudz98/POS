package com.casecode.pos.ui.stepper

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.casecode.testing.BaseTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class StepperActivityTest:BaseTest()
{
   @get:Rule
   var hiltRule = HiltAndroidRule(this)
   
   
   override fun init()
   {
   
   }
 
}