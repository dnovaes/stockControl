package com.dnovaes.stockcontrol.features.updateproduct.models

import android.graphics.Bitmap
import com.dnovaes.stockcontrol.common.models.UIModelInterface


data class UpdateUIModel(
    val registerImage: Bitmap? = null,
): UIModelInterface
