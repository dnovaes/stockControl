package com.dnovaes.stockcontrol.common.models

import com.dnovaes.stockcontrol.R

enum class ErrorCode: ErrorCodeInterface {
    HTTP_422 {
        override val resId = R.string.error_code_422
    },
    UNKNOWN_EXCEPTION {
        override val resId = R.string.unknown_exception
    },
    INVALID_PRODUCT_MODEL{
        override val resId = R.string.invalid_product_model
    }
}