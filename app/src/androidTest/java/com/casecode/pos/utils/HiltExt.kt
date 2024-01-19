package com.casecode.pos.utils

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Preconditions
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.casecode.pos.HiltTestActivity
import com.casecode.pos.R
import com.casecode.pos.ui.stepper.StepperActivity

const val THEME_EXTRAS_BUNDLE_KEY =
   "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY"


/**
 * launchFragmentInContainer from the androidx.fragment:fragment-testing library
 * is NOT possible to use right now as it uses a hardcoded Activity under the hood
 * (i.e. [EmptyFragmentActivity]) which is not annotated with @AndroidEntryPoint.
 *
 * As a workaround, use this function that is equivalent. It requires you to add
 * [HiltTestActivity] in the debug folder and include it in the debug AndroidManifest.xml file
 * as can be found in this project.
 */
inline fun <reified T : Fragment> launchFragmentInHiltContainer(
     fragmentArgs: Bundle? = null,
     themeResId: Int = R.style.Theme_POS,
     fragmentFactory: FragmentFactory? = null,
     crossinline action: T.() -> Unit = {}, )
{
   val mainActivityIntent = Intent.makeMainActivity(
      ComponentName(
         ApplicationProvider.getApplicationContext(),
         HiltTestActivity::class.java
                   )
                                                   ).putExtra(THEME_EXTRAS_BUNDLE_KEY, themeResId)
   ActivityScenario.launch<HiltTestActivity>(mainActivityIntent).onActivity { activity ->
      fragmentFactory?.let {
         activity.supportFragmentManager.fragmentFactory = it
      }
      val fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
         Preconditions.checkNotNull(T::class.java.classLoader),
         T::class.java.name)
      
      fragment.arguments = fragmentArgs
      activity.supportFragmentManager.beginTransaction()
         .add(android.R.id.content, fragment, "")
         .commitNow()
      (fragment as T).action()
   }
}

inline fun <reified T : Fragment, reified A : AppCompatActivity> launchFragmentInHiltContainerWithActivity(
     @StyleRes themeResId: Int = R.style.Theme_POS,
     fragmentFactory: FragmentFactory? = null,
     
     crossinline action: T.() -> Unit = {},
     )
{

   
   val mainActivityIntent = Intent.makeMainActivity(
      ComponentName(
         ApplicationProvider.getApplicationContext(),
         A::class.java))
      .putExtra(THEME_EXTRAS_BUNDLE_KEY, themeResId)
   
   ActivityScenario.launch<A>(mainActivityIntent).onActivity { activity ->
      fragmentFactory?.let {
         activity.supportFragmentManager.fragmentFactory = it
      }
   /*    val fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
         Preconditions.checkNotNull(T::class.java.classLoader),
         T::class.java.name)
      
      fragment.arguments = fragmentArgs
      activity.supportFragmentManager.beginTransaction()
         .add(android.R.id.content, fragment, "")
         .commitNow()
      (fragment as T).action() */
  
   }
}
