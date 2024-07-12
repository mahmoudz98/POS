package com.casecode.pos.feature.signout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.data.service.AccountService
import com.casecode.pos.core.data.service.AuthService
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SignOutViewModel @Inject constructor(
    private val accountService: AccountService,
    authService: AuthService,
) : ViewModel() {
    val userUiState: StateFlow<FirebaseUser?> = authService.currentUser
        .stateIn(
            scope = viewModelScope,
            initialValue = null,
            started = SharingStarted.WhileSubscribed(1_000),
        )

    suspend fun signOut() : Deferred<Unit> {
       return viewModelScope.async {
            accountService.signOut()
        }
    }
}