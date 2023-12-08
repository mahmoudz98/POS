package com.casecode.pos.ui.business

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OpenForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.casecode.pos.R
import com.casecode.pos.base.BaseTextWatcher
import com.casecode.pos.databinding.FragmentAddBusinessBinding
import com.casecode.pos.utils.showSnackbar
import com.casecode.pos.viewmodel.BusinessViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AddBusinessFragment : Fragment()
{
   
   private var _binding: FragmentAddBusinessBinding? = null
   
   
   val binding get() = _binding !!
   
   @get:OpenForTesting
   val businessViewModel by activityViewModels<BusinessViewModel>()
   
   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
                            ): View
   {
      _binding = FragmentAddBusinessBinding.inflate(inflater, container, false)
      
      return binding.root
   }
   
   override fun onViewCreated(view: View, savedInstanceState: Bundle?)
   {
      super.onViewCreated(view, savedInstanceState)
      init()
   }
   
   private fun init()
   {
      initViewModel()
      
      validateAddBusiness()
      initAddBusiness()
   }
   
   private fun initViewModel()
   {
      binding.apply {
         viewModel = businessViewModel
         business.viewModel = businessViewModel
      }
      // Set the lifecycle owner to the lifecycle of the view
      binding.lifecycleOwner = this.viewLifecycleOwner
   }
   

   
   
   private fun validateAddBusiness()
   {
      
      binding.business.etBusinessMail.addTextChangedListener(object : BaseTextWatcher()
      {
         override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
         {
            
            if (TextUtils.isEmpty(s))
            {
               binding.business.tilBusinessMail.boxStrokeErrorColor
               binding.business.tilBusinessMail.error =
                  getString(R.string.add_business_email_empty)
               
            } else if (! s.toString().trim { it <= ' ' }
                  .matches(Patterns.EMAIL_ADDRESS.toString().toRegex()))
            {
               binding.business.tilBusinessMail.error =
                  getString(R.string.add_business_email_invalid)
            } else
            {
               binding.business.tilBusinessMail.boxStrokeColor =
                  resources.getColor(R.color.md_theme_light_primary, requireActivity().theme)
               binding.business.tilBusinessMail.error = null
            }
            
         }
         
      })
      binding.business.etBusinessPhone.addTextChangedListener(object : BaseTextWatcher()
      {
         override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
         {
            
            if (TextUtils.isEmpty(s))
            {
               binding.business.tilBusinessPhone.boxStrokeErrorColor
               binding.business.tilBusinessPhone.error =
                  getString(R.string.add_business_phone_empty)
               
            } else if (! s.toString().trim { it <= ' ' }
                  .matches(Patterns.PHONE.toString().toRegex()))
            {
               binding.business.tilBusinessPhone.error =
                  getString(R.string.add_business_phone_invalid)
            } else
            {
               binding.business.tilBusinessPhone.boxStrokeColor =
                  resources.getColor(R.color.md_theme_light_primary, requireActivity().theme)
               binding.business.tilBusinessPhone.error = null
            }
            
         }
         
      })
      
      
   }
   
   
   private fun initAddBusiness()
   {
      binding.btnAddBusinessBranches.setOnClickListener {
         
         if (isAddBusinessValid())
         {
            businessViewModel.moveToNextStep()
            
         }
         
      }
   }
   
   private fun isAddBusinessValid(): Boolean
   {
      val storeType = binding.business.actvBusiness.text.toString()
      val email = binding.business.etBusinessMail.text.toString()
      val phone = binding.business.etBusinessPhone.text.toString()
      // Check login and pass are empty
      if (TextUtils.isEmpty(storeType) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone)
         || ! email.trim { it <= ' ' }
            .matches(Patterns.EMAIL_ADDRESS.toString().toRegex()) ||
         ! phone.trim { it <= ' ' }
            .matches(Patterns.PHONE.toString().toRegex())
      )
      {
         binding.root.showSnackbar(getString(R.string.add_business_empty), Snackbar.LENGTH_LONG)
         
         if (TextUtils.isEmpty(storeType))
         {
            binding.business.tilBusinessType.error =
               getString(R.string.add_business_store_type_empty)
         }
         if (TextUtils.isEmpty(email))
         {
            binding.business.tilBusinessMail.error =
               getString(R.string.add_business_email_empty)
         }
         if (TextUtils.isEmpty(phone))
         {
            binding.business.tilBusinessPhone.error =
               getString(R.string.add_business_phone_empty)
         }
         if (! email.trim { it <= ' ' }
               .matches(Patterns.EMAIL_ADDRESS.toString().toRegex()))
         {
            binding.business.tilBusinessMail.error =
               getString(R.string.add_business_email_invalid)
         }
         if (! phone.trim { it <= ' ' }
               .matches(Patterns.PHONE.toString().toRegex()))
         {
            binding.business.tilBusinessPhone.error =
               getString(R.string.add_business_phone_invalid)
         }
         
         return false
      }
      businessViewModel.setStoreType(storeType.lowercase(Locale.ENGLISH))
      businessViewModel.setEmail(email)
      businessViewModel.setPhone(phone)
      
      return true
      
   }
   
   
   override fun onDestroyView()
   {
      super.onDestroyView()
      _binding = null
   }
}