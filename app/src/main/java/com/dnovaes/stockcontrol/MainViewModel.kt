package com.dnovaes.stockcontrol

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.dnovaes.stockcontrol.ui.State

class MainViewModel: ViewModel() {

    private var _gameState = State.INITIAL
    val gameState: MutableState<State> = mutableStateOf(_gameState)

    fun startCapture() {
        _gameState = State.CAPTURING
        gameState.value = _gameState
    }

    fun finishCapturing() {
        _gameState = State.DONE
        gameState.value = _gameState
    }

    fun resetState() {
        _gameState = State.INITIAL
        gameState.value = _gameState
    }
}