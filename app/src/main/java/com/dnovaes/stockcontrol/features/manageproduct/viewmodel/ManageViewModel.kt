package com.dnovaes.stockcontrol.features.manageproduct.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.dnovaes.stockcontrol.common.models.State
import com.dnovaes.stockcontrol.features.manageproduct.models.ManageProcess
import com.dnovaes.stockcontrol.features.manageproduct.models.ManageUIModel
import com.dnovaes.stockcontrol.features.manageproduct.models.ManageUIObservable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    fun requestCamera() {
        _state = _state.asRequestingCamera()
        state.value = _state
    }

    fun finishCapturing() {
        _state = _state.asDoneRequestCamera()
        state.value = _state
    }

    fun snackBarShown() {
        _state = _state
            .asIdle()
            .withError(null)
        state.value = _state
    }
}
