package com.casecode.pos.ui.signout

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import coil.load
import coil.request.ImageRequest
import com.casecode.pos.R
import com.casecode.pos.databinding.DialogSignOutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SignOutDialog : DialogFragment()
{
   
   private lateinit var binding: DialogSignOutBinding
   
   @Inject
   lateinit var auth: FirebaseAuth
   
   interface SignOutDialogListener
   {
      fun onSignOut()
   }
   
   private var listener: SignOutDialogListener? = null
   
   override fun onAttach(context: Context)
   {
      super.onAttach(context)
      if (context is SignOutDialogListener)
      {
         listener = context
      }
   }
   
   override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
                            ): View
   {
      binding.apply {
         // Get the string argument passed to the fragment and set it in the TextView
         
         // name
         val name = auth.currentUser?.displayName
         
         tvName.text = name
         
         // email
         val email = auth.currentUser?.email
         tvEmail.text = email
         
         
         val photoUrl = auth.currentUser?.photoUrl
        
         Timber.e("photoUrl = $photoUrl")
         ivPhoto.load(photoUrl) {
            placeholder(R.drawable.ic_google)
            error(R.drawable.ic_google)
         }
         
         
         
         ivClose.setOnClickListener {
            dismiss()
         }
         
         btnSignOut.setOnClickListener {
            listener?.onSignOut()
            dismiss()
         }
      }
      
      isCancelable = false
      return binding.root
   }
   
   override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
   {
      val builder = MaterialAlertDialogBuilder(
         requireContext())
      binding = DialogSignOutBinding.inflate(layoutInflater)
      
      builder.setView(binding.root)
      builder.setCancelable(false)
      
      return builder.create()
   }
   
}