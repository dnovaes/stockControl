package com.dnovaes.stockcontrol.features.manageproduct.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.dnovaes.stockcontrol.common.models.ErrorCode
import com.dnovaes.stockcontrol.common.models.ErrorCodeInterface
import com.dnovaes.stockcontrol.common.models.State
import com.dnovaes.stockcontrol.common.models.UIErrorInterface
import com.dnovaes.stockcontrol.common.models.business.StockProduct
import com.dnovaes.stockcontrol.common.monitoring.log
import com.dnovaes.stockcontrol.common.utils.SessionManager
import com.dnovaes.stockcontrol.features.manageproduct.models.ManageProcess
import com.dnovaes.stockcontrol.features.manageproduct.models.ManageUIModel
import com.dnovaes.stockcontrol.features.manageproduct.models.ManageUIObservable
import com.dnovaes.stockcontrol.type.NewStockProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageViewModel @Inject constructor(
    private val apolloClient: ApolloClient
): ViewModel() {

    private val initialObservable = ManageUIObservable(
        state = State.IDLE,
        process = ManageProcess.LOAD_INITIAL_DATA,
        data = ManageUIModel(),
    )

    private var _state = initialObservable
    val state: MutableState<ManageUIObservable> = mutableStateOf(_state)

    fun resetState() {
        _state = initialObservable
        state.value = _state
    }

    fun searchProductByName(input: String) {
        //do Nothing
    }

    fun loadInitialData() {
        _state = _state.asLoadingInitialData()
        state.value = _state

        viewModelScope.launch {
            delay(2000)

            _state = _state.asDoneLoadInitialData()
            state.value = _state
        }
    }
}
