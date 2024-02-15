package com.dnovaes.stockcontrol.common.utils

import com.dnovaes.stockcontrol.common.models.business.Product
import com.dnovaes.stockcontrol.common.models.business.ProductCategory
import com.dnovaes.stockcontrol.common.models.business.StockProduct


object SessionManager {

    private var productCategories: List<ProductCategory> = emptyList()
    private var products: Map<String, Product> = emptyMap<String, Product>()
    private var stockProducts: Map<String, StockProduct> = emptyMap()

    val storeId = "1"

    fun saveProductCategories(categories: List<ProductCategory>) {
        productCategories = categories
    }

    fun loadProductCategories(): List<ProductCategory> = productCategories

    fun saveProduct(product: Product) {
        val newProducts = products.toMutableMap()
        newProducts[product.id] = product
        products = newProducts.toMap()
    }

    fun saveStockProduct(product: StockProduct) {
        val newStockProducts = stockProducts.toMutableMap()
        newStockProducts[product.productId] = product
        stockProducts = newStockProducts.toMap()
    }

    fun loadLocalProductInfo(productId: String): Product? {
        return products.values.firstOrNull { it.id == productId }
    }
}
