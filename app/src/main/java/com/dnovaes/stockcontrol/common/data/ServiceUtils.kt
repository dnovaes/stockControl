package com.dnovaes.stockcontrol.common.data

import com.dnovaes.stockcontrol.common.models.ErrorCode
import com.dnovaes.stockcontrol.common.models.ErrorCodeInterface
import com.dnovaes.stockcontrol.common.models.UIErrorInterface
import com.dnovaes.stockcontrol.common.monitoring.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException

suspend fun CoroutineScope.requestSafely(
    logTag: String,
    block: suspend () -> Unit,
    onError: (UIErrorInterface) -> Unit
) {
    try {
        kotlinx.coroutines.withTimeout(8000) {
            block()
        }
    } catch (e: TimeoutCancellationException) {
        log("$logTag) ${e.stackTraceToString()}")
        val error = object : UIErrorInterface {
            override val errorCode: ErrorCodeInterface = ErrorCode.TIME_OUT_EXCEPTION
            override val additionalParams: Map<String, String> = emptyMap()
        }
        onError(error)
    } catch (e: Exception) {
        log("$logTag) Exception: ${e.localizedMessage}, ${e.stackTraceToString()}")
        val error = object : UIErrorInterface {
            override val errorCode: ErrorCodeInterface = ErrorCode.UNKNOWN_EXCEPTION
            override val additionalParams: Map<String, String> = emptyMap()
        }
        onError(error)
    }
}
