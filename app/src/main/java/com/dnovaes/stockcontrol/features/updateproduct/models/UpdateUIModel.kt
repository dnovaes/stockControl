package com.dnovaes.stockcontrol.features.updateproduct.models

import android.graphics.Bitmap
import com.dnovaes.stockcontrol.common.models.UIModelInterface
import com.dnovaes.stockcontrol.common.models.business.Product
import com.dnovaes.stockcontrol.common.models.business.StockProduct


data class UpdateUIModel(
    val registerImage: Bitmap? = null,
    val productInfo: Product? = null,
    val lastAddedStockProduct: StockProduct? = null
): UIModelInterface
