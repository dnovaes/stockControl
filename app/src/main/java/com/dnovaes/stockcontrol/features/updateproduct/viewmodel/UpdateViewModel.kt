package com.dnovaes.stockcontrol.features.updateproduct.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnovaes.stockcontrol.features.updateproduct.models.UpdateProcess
import com.dnovaes.stockcontrol.features.updateproduct.models.UpdateUIModel
import com.dnovaes.stockcontrol.features.updateproduct.models.UpdateUIObservable
import com.dnovaes.stockcontrol.ui.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UpdateViewModel: ViewModel() {

    private val initialObservable = UpdateUIObservable(
        state = State.START,
        process = UpdateProcess.LOAD_INITIAL_DATA,
        data = UpdateUIModel(),
    )

    private var _updateState = initialObservable
    val updateState: MutableState<UpdateUIObservable> = mutableStateOf(_updateState)


    fun resetState() {
        _updateState = initialObservable
        updateState.value = _updateState
    }

    fun updateProduct() {
        viewModelScope.launch(Dispatchers.IO) {
            _updateState = _updateState.asUpdatingProduct()
            updateState.value = _updateState

            delay(2000)

            _updateState = _updateState.asDoneUpdateProduct()
            updateState.value = _updateState
        }
    }
}
