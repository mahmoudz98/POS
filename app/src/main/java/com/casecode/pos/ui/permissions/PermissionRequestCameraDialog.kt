package com.casecode.pos.ui.permissions

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.casecode.pos.R
import com.casecode.pos.base.PositiveDialogListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Author: Mahmoud Abdalhafeez
 * Created: 2/6/2024
 * Description: shown when permission camera denied.
 */
class PermissionRequestCameraDialog: DialogFragment()
{
   // Use this instance of the interface to deliver action events.
    internal lateinit var listener: PositiveDialogListener
    
   override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
   {
     
      return activity?.let {
         // Build the dialog and set up the button click handlers.
         val builder = MaterialAlertDialogBuilder(it)
         
         builder.setTitle(R.string.title_permission_dialog)
            .setMessage(R.string.message_permission_dialog)
            .setPositiveButton(R.string.positive_text_dialog
                              ) { _, _ ->
               // Send the positive button event back to the
               // host activity.
               listener.onDialogPositiveClick()
            }
            .setNegativeButton(R.string.cancel
                              ) { _, _ ->
               
               dismiss()
            }
         
         builder.create()
      } ?: throw IllegalStateException("Activity cannot be null")
   }
}