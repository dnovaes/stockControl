package com.dnovaes.stockcontrol.features.addproduct.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnovaes.stockcontrol.features.addproduct.models.AddProcess
import com.dnovaes.stockcontrol.features.addproduct.models.AddUIModel
import com.dnovaes.stockcontrol.features.addproduct.models.AddUIObservable
import com.dnovaes.stockcontrol.ui.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddViewModel: ViewModel() {

    private val initialObservable = AddUIObservable(
        state = State.INITIAL,
        process = AddProcess.LOAD_INITIAL_DATA,
        data = AddUIModel(),
    )

    private var _addState = initialObservable
    val addState: MutableState<AddUIObservable> = mutableStateOf(_addState)


    fun resetState() {
        _addState = initialObservable
        addState.value = _addState
    }

    fun finishImageCapturing(image: Bitmap?) {
        val model = _addState.data
        val newModel = model.copy(registerImage = image)

        _addState = _addState.copy(data = newModel)
        addState.value = _addState
    }

    fun registerProduct() {
        viewModelScope.launch(Dispatchers.IO) {
            _addState = _addState.asAddingProduct()
            addState.value = _addState

            delay(4000)

            _addState = _addState.asDoneAddProduct()
            addState.value = _addState
        }
    }
}
