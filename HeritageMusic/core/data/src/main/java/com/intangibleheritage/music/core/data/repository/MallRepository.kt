package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.MallSection
import com.intangibleheritage.music.core.data.model.Product

interface MallRepository {
    fun products(): List<Product>
    fun productById(id: String): Product?
    fun productsInSection(section: MallSection): List<Product>
}
