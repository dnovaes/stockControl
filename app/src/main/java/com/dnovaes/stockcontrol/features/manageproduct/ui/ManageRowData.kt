package com.dnovaes.stockcontrol.features.manageproduct.ui

data class ManageRowData(
    val itemTitle: String,
    val itemQuantity: String,
    val itemPrice: String
) {
    companion object {
        val header = ManageRowData(
            itemTitle = "Nome",
            itemQuantity = "Estoque",
            itemPrice = "Preço"
        )
    }
}