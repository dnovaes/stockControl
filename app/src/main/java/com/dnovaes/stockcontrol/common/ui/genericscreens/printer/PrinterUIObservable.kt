package com.dnovaes.stockcontrol.common.ui.genericscreens.printer

import android.graphics.Bitmap
import com.dnovaes.stockcontrol.common.models.UIErrorInterface
import com.dnovaes.stockcontrol.common.models.UIModelInterface
import com.dnovaes.stockcontrol.common.models.UIObservable
import com.dnovaes.stockcontrol.common.models.UIProcessInterface
import com.dnovaes.stockcontrol.ui.State

enum class PrinterProcess: UIProcessInterface {
    REQUEST_PRINT,
    GET_STATUS
}

data class PrinterUIModel(
    val image: Bitmap? = null
): UIModelInterface

data class PrinterUIObservable(
    override val state: State,
    override val process: PrinterProcess,
    override val data: PrinterUIModel,
    override val error: UIErrorInterface? = null
): UIObservable<PrinterUIModel>(state, process, data, error) {

    fun withError(error: UIErrorInterface?)
        = this.copy(error = error)

    override fun withData(model: PrinterUIModel) = this.copy(data = data)

    fun withState(state: State): PrinterUIObservable
            = this.copy(state = state)

    //Customized methods

    fun asIdle(): PrinterUIObservable = copy(state = State.IDLE)

    //region: REQUEST_PRINT

    fun asStartPrint(): PrinterUIObservable {
        return copy(
            state = State.START,
            process = PrinterProcess.REQUEST_PRINT
        )
    }

    fun asProcessingPrint(): PrinterUIObservable {
        return copy(
            state = State.PROCESSING,
            process = PrinterProcess.REQUEST_PRINT
        )
    }

    fun asDonePrint(): PrinterUIObservable {
        return copy(
            state = State.DONE,
            process = PrinterProcess.REQUEST_PRINT
        )
    }

    fun isProcessingPrint(): Boolean =
        state == State.PROCESSING &&
        process == PrinterProcess.REQUEST_PRINT

    fun isDonePrint(): Boolean =
        state == State.DONE &&
                process == PrinterProcess.REQUEST_PRINT

    //endregion
}