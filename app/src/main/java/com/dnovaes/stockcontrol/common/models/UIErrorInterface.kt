package com.dnovaes.stockcontrol.common.models

interface UIErrorInterface {
    val errorCode: ErrorCodeInterface
    val additionalParams: Map<String, String>
}

interface ErrorCodeInterface {
    val resId: Int
}

