package com.dnovaes.stockcontrol.common.models

interface UIErrorInterface {
    val errorCode: ErrorCodeInterface
}

interface ErrorCodeInterface {
    val resId: Int
}

