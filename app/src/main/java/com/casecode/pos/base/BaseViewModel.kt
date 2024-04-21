package com.casecode.pos.base

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.casecode.pos.utils.Event

abstract class BaseViewModel : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _userMessage: MutableLiveData<Event<Int>> = MutableLiveData()
    val userMessage get() = _userMessage
    private val _currentUid: MutableLiveData<String> = MutableLiveData()
    val currentUid: LiveData<String> get() = _currentUid

    fun setCurrentUid(currentUid: String) {
        if (currentUid.isBlank()) {
            _currentUid.value = ""
        } else {
            _currentUid.value = currentUid
        }
    }

   protected fun showProgress() {
        _isLoading.value = true
    }

    protected  fun hideProgress() {
        _isLoading.value = false
    }
    protected fun showSnackbarMessage(
        @StringRes message: Int,
    ) {
        _userMessage.value = Event(message)
    }
}