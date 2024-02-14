package com.dnovaes.stockcontrol.common.models.business

import com.dnovaes.stockcontrol.common.models.UIModelInterface

data class Product(
    val id: String,
    val name: String,
    val imageLogo: String,
    val categoryId: String,
    val brand: String,
    val supplier: String
): UIModelInterface