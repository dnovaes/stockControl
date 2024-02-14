package com.dnovaes.stockcontrol.features.addproduct.models

import android.graphics.Bitmap
import com.dnovaes.stockcontrol.common.models.UIModelInterface
import com.dnovaes.stockcontrol.common.models.business.ProductCategory


data class AddUIModel(
    val registerImage: Bitmap? = null,
    val categories: List<ProductCategory> = emptyList()
): UIModelInterface
