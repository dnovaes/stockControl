package com.dnovaes.stockcontrol.common.ui.genericscreens.printer

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnovaes.stockcontrol.ui.State
import com.dnovaes.stockcontrol.utilities.StockBluetoothManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrintPreviewVIewModel(
    private val bluetoothManager: StockBluetoothManager
): ViewModel() {

    private val initialObservable = PrinterUIObservable(
        state = State.IDLE,
        process = PrinterProcess.REQUEST_PRINT,
        data = PrinterUIModel(),
    )

    private var _uiState = initialObservable
    val printState: MutableState<PrinterUIObservable> = mutableStateOf(_uiState)

    fun userRequestedToPrint(image: Bitmap) {
        if (bluetoothManager.isConnected) {
            _uiState = _uiState
                .withData(PrinterUIModel(image))
                .asProcessingPrint()
            printState.value = _uiState

            viewModelScope.launch(Dispatchers.IO) {
                bluetoothManager.printWithClient(
                    image = image,
                    onErrorPrinting = {
                        _uiState = _uiState
                            .asDonePrint()
                            .withError(
                                PrinterUIError(PrinterErrorCode.PRINTER_GENERAL_ERROR)
                            )
                        printState.value = _uiState
                    },
                    onFinishPrinting = {
                        _uiState = _uiState.asDonePrint()
                        printState.value = _uiState
                    }
                )
            }
        } else {
            _uiState = _uiState
                .asDonePrint()
                .withError(
                    PrinterUIError(PrinterErrorCode.PRINTER_NOT_CONNECTED)
                )
            printState.value = _uiState
        }

    }

    fun snackBarShown() {
        _uiState = _uiState
            .asIdle()
            .withError(null)
        printState.value = _uiState
    }
}