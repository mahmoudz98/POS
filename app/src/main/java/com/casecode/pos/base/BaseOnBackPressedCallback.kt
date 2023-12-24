package com.casecode.pos.base

import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.slidingpanelayout.widget.SlidingPaneLayout

/**
 * Created by Mahmoud Abdalhafeez on 12/20/2023.
 *
 * Callback providing custom back navigation.
 */
 class BaseOnBackPressedCallback internal constructor(private val mSlidingPaneLayout: SlidingPaneLayout) :
   OnBackPressedCallback(mSlidingPaneLayout.isSlideable && mSlidingPaneLayout.isOpen),
   SlidingPaneLayout.PanelSlideListener
{
   init
   {
      // Set the default 'enabled' state to true only if it is slideable (i.e., the panes
      // are overlapping) and open (i.e., the detail pane is visible).
      mSlidingPaneLayout.addPanelSlideListener(this)
   }
   
   override fun handleOnBackPressed()
   {
      // Return to the list pane when the system back button is pressed.
      mSlidingPaneLayout.closePane()
   }
   
   override fun onPanelSlide(panel: View, slideOffset: Float)
   {
      //NO thing.
   }
   
   override fun onPanelOpened(panel: View)
   {
      // Intercept the system back button when the detail pane becomes visible.
      isEnabled = true
   }
   
   override fun onPanelClosed(panel: View)
   {
      // Disable intercepting the system back button when the user returns to the
      // list pane.
      isEnabled = false
   }
}