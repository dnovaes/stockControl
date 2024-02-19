package com.dnovaes.stockcontrol.features.manageproduct.models

import com.dnovaes.stockcontrol.common.models.UIModelInterface
import com.dnovaes.stockcontrol.common.models.business.StockProduct


data class ManageUIModel(
    val products: List<StockProduct> = listOf()
): UIModelInterface
