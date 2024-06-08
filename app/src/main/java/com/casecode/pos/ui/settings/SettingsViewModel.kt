package com.casecode.pos.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() :
    ViewModel() {
     private val _currentLanguage: MutableLiveData<String> = MutableLiveData()
    val currentLanguage  get() = _currentLanguage

    fun setCurrentLanguage(value: String?) {
        _currentLanguage.value = value ?: return

    }



}