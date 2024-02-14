package com.dnovaes.stockcontrol.common.models.business

import com.dnovaes.stockcontrol.common.models.UIModelInterface

data class ProductCategory(
    val id: String,
    val name: String
): UIModelInterface {
    companion object {
        val EMPTY_CATEGORY = ProductCategory(
            id = "-1",
            name = "NoCategorySet"
        )
    }
}