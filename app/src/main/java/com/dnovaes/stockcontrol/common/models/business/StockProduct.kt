package com.dnovaes.stockcontrol.common.models.business

import com.dnovaes.stockcontrol.common.models.UIModelInterface

data class StockProduct(
    val productId: String,
    val productName: String,
    val storeId: String,
    val styleColor: String,
    val size: String,
    val sku: String,
    val price: String,
    val quantity: Int
): UIModelInterface
