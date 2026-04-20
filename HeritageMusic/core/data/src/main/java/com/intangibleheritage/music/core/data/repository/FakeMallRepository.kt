package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.Product
import com.intangibleheritage.music.core.resources.R

class FakeMallRepository : MallRepository {

    private val items: List<Product> = listOf(
        Product(
            id = "dunhuang_magnet",
            titleRes = R.string.product_dunhuang_magnet,
            priceYuan = 30,
            rating = 4.9f,
            reviewCount = 12,
            descriptionRes = R.string.product_desc_dunhuang,
            imageRes = R.drawable.product_dunhuang
        ),
        Product(
            id = "bronze_bells",
            titleRes = R.string.product_bronze_bells,
            priceYuan = 128,
            rating = 4.7f,
            reviewCount = 8,
            descriptionRes = R.string.product_desc_bronze,
            imageRes = R.drawable.product_bronze_bells
        ),
        Product(
            id = "silk_scarf",
            titleRes = R.string.product_silk_scarf,
            priceYuan = 199,
            rating = 4.8f,
            reviewCount = 21,
            descriptionRes = R.string.product_desc_silk,
            imageRes = R.drawable.product_silk_scarf
        ),
        Product(
            id = "pipa_bookmark",
            titleRes = R.string.product_pipa_model,
            priceYuan = 25,
            rating = 4.6f,
            reviewCount = 15,
            descriptionRes = R.string.product_desc_pipa,
            imageRes = R.drawable.product_pipa_bookmark
        )
    )

    override fun products(): List<Product> = items

    override fun productById(id: String): Product? = items.find { it.id == id }
}
