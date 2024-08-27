package com.casecode.pos.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.data.utils.NetworkMonitor
import com.casecode.pos.core.domain.usecase.GetTodayInvoicesUseCase
import com.casecode.pos.core.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel
    @Inject
    constructor(
        private val networkMonitor: NetworkMonitor,
        private val getTodayInvoicesUseCase: GetTodayInvoicesUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(UiReportsState())
        val uiState = _uiState.asStateFlow()

    fun fetchInvoices() {
        viewModelScope.launch {
            getTodayInvoicesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Empty -> {
                        _uiState.update { it.copy(isEmpty = true, isLoading = false) }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                userMessage = resource.message as? Int,
                                isEmpty = true,
                                isLoading = false,
                            )
                        }
                    }

                    Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        _uiState.update { it.copy(invoices = resource.data, isLoading = false) }
                    }
                }
            }
        }
    }
}