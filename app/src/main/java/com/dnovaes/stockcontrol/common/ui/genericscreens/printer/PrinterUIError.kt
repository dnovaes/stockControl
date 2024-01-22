package com.dnovaes.stockcontrol.common.ui.genericscreens.printer

import com.dnovaes.stockcontrol.R
import com.dnovaes.stockcontrol.common.models.ErrorCodeInterface
import com.dnovaes.stockcontrol.common.models.UIErrorInterface

data class PrinterUIError(
    override val errorCode: PrinterErrorCode,
    val extra: Map<String, String> = emptyMap()
): UIErrorInterface

enum class PrinterErrorCode(override val resId: Int): ErrorCodeInterface {
    PRINTER_NOT_CONNECTED(resId = R.string.printer_not_connected),
    PRINTER_GENERAL_ERROR(resId = R.string.printer_generic_error)
}