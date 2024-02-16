package com.dnovaes.stockcontrol.features.updateproduct.models

import android.graphics.Bitmap
import com.dnovaes.stockcontrol.common.models.UIModelInterface
import com.dnovaes.stockcontrol.common.models.business.Product
import com.dnovaes.stockcontrol.common.models.business.ProductSize
import com.dnovaes.stockcontrol.common.models.business.StockProduct


data class UpdateUIModel(
    val registerImage: Bitmap? = null,
    val productInfo: Product? = null,
    val lastAddedStockProduct: StockProduct? = null,
    val sizes: List<ProductSize> = listOf(
        ProductSize("0", "U"),
        ProductSize("1", "PPP"),
        ProductSize("2", "PP"),
        ProductSize("3", "P"),
        ProductSize("4", "M"),
        ProductSize("5", "G"),
        ProductSize("6", "GG"),
        ProductSize("7", "GGG"),
    )
): UIModelInterface
