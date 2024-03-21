package com.dnovaes.stockcontrol.common.monitoring

import android.util.Log

fun log(msg: String) {
    Log.d("DEBUG", msg)
}

fun logSentRequest(tag: String, message: String) {
    Log.d("DEBUG", "$tag) - Sent Service Request - $message")
}

fun logFailedRequest(tag: String, message: String) {
    Log.d("DEBUG", "$tag) - Failed Service Response - $message")
}

fun logSuccessRequest(tag: String, message: String) {
    Log.d("DEBUG", "$tag) - Succeeded Service Response - $message")
}