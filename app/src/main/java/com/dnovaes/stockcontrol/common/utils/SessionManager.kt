package com.dnovaes.stockcontrol.common.utils

import com.dnovaes.stockcontrol.common.models.business.Product
import com.dnovaes.stockcontrol.common.models.business.ProductCategory


object SessionManager {

    private var productCategories: List<ProductCategory> = emptyList()
    private var products: Map<String, Product> = emptyMap<String, Product>()

    fun saveProductCategories(categories: List<ProductCategory>) {
        productCategories = categories
    }

    fun loadProductCategories(): List<ProductCategory> = productCategories

    fun saveProduct(product: Product) {
        val newProducts = products.toMutableMap()
        newProducts[product.id] = product
        products = newProducts.toMap()
    }

    fun loadLocalProductInfo(productId: String): Product? {
        return products.values.firstOrNull { it.id == productId }
    }
}
