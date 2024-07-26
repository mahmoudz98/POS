package com.casecode.pos.feature.setting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.data.service.AuthService
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    authService: AuthService,
) :
    ViewModel() {
    val userUiState: StateFlow<FirebaseUser?> = authService.currentUser
        .stateIn(
            scope = viewModelScope,
            initialValue = null,
            started = SharingStarted.WhileSubscribed(1_000),
        )
     private val _currentLanguage: MutableLiveData<String> = MutableLiveData()
    val currentLanguage  get() = _currentLanguage

    fun setCurrentLanguage(value: String?) {
        _currentLanguage.value = value ?: return

    }



}