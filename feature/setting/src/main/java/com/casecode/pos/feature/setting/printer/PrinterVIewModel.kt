package com.casecode.pos.feature.setting.printer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.domain.usecase.AddPrinterUseCase
import com.casecode.pos.core.domain.usecase.GetPrinterUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.PrinterInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PrinterVIewModel @Inject constructor(
    private val addPrinterUseCase: AddPrinterUseCase,
    private val getPrinterUseCase: GetPrinterUseCase,
) : ViewModel() {
    val printersUiState: StateFlow<Resource<List<PrinterInfo>>> = getPrinterUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Resource.Loading,
        )

}