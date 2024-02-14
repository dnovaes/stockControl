package com.dnovaes.stockcontrol.common.utils

import com.dnovaes.stockcontrol.common.models.business.ProductCategory


object SessionManager {

    private var productCategories: List<ProductCategory> = emptyList()

    fun saveProductCategories(categories: List<ProductCategory>) {
        productCategories = categories
    }

    fun loadProductCategories(): List<ProductCategory> = productCategories
}
