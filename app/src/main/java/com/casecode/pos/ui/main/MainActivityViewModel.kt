package com.casecode.pos.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Author: Mahmoud Abdalhafeez
 * Created: 1/5/2024
 * Description:
 */
@HiltViewModel
class MainActivityViewModel @Inject constructor(private val signOutUseCase: SignOutUseCase): ViewModel()
{
   fun signOut(){
      viewModelScope.launch {
         signOutUseCase()
      }
   }
}
sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data object IsSignedIn : MainActivityUiState
}