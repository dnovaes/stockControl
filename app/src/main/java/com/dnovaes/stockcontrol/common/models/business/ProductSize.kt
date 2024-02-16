package com.dnovaes.stockcontrol.common.models.business

import com.dnovaes.stockcontrol.common.models.UIModelInterface

data class ProductSize(
    val id: String,
    val code: String,
): UIModelInterface {
    companion object {
        val UNIQUE_SIZE = ProductSize(id = "0", code = "U")
    }
}