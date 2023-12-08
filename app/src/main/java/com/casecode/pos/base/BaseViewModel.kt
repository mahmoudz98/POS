package com.casecode.pos.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.casecode.pos.utils.Event
import com.casecode.pos.utils.EventObserver

abstract class BaseViewModel : ViewModel()
{
   private val _isLoading = MutableLiveData(false)
   val isLoading: LiveData<Boolean> get() = _isLoading
   
   private val _currentUid: MutableLiveData<String> = MutableLiveData()
   val currentUid: LiveData<String> get() = _currentUid
   fun setCurrentUid(currentUid: String)
   {
      if(currentUid.isBlank()){
         //TODO: handle when no uid
         _currentUid.value = ("Error")
      }else{
      
      _currentUid.value = currentUid
      }
   }
   
   fun showProgress()
   {
      _isLoading.value = true
   }
   
   fun hideProgress()
   {
      _isLoading.value = false
   }
}