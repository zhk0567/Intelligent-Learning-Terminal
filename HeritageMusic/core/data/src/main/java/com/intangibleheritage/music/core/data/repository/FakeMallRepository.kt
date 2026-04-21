package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.MallSection
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
            imageRes = R.drawable.product_dunhuang,
            section = MallSection.Story
        ),
        Product(
            id = "story_shadow_charm",
            titleRes = R.string.product_story_shadow,
            priceYuan = 42,
            rating = 4.8f,
            reviewCount = 9,
            descriptionRes = R.string.product_desc_story_shadow,
            imageRes = R.drawable.product_dunhuang,
            section = MallSection.Story
        ),
        Product(
            id = "story_canvas_print",
            titleRes = R.string.product_story_canvas,
            priceYuan = 158,
            rating = 4.7f,
            reviewCount = 6,
            descriptionRes = R.string.product_desc_story_canvas,
            imageRes = R.drawable.product_silk_scarf,
            section = MallSection.Story
        ),
        Product(
            id = "silk_scarf",
            titleRes = R.string.product_silk_scarf,
            priceYuan = 199,
            rating = 4.8f,
            reviewCount = 21,
            descriptionRes = R.string.product_desc_silk,
            imageRes = R.drawable.product_silk_scarf,
            section = MallSection.Cultural
        ),
        Product(
            id = "cultural_bookmark_set",
            titleRes = R.string.product_cultural_bookmarks,
            priceYuan = 56,
            rating = 4.6f,
            reviewCount = 11,
            descriptionRes = R.string.product_desc_cultural_bookmarks,
            imageRes = R.drawable.product_pipa_bookmark,
            section = MallSection.Cultural
        ),
        Product(
            id = "cultural_palace_lantern",
            titleRes = R.string.product_cultural_lantern,
            priceYuan = 88,
            rating = 4.7f,
            reviewCount = 7,
            descriptionRes = R.string.product_desc_cultural_lantern,
            imageRes = R.drawable.product_bronze_bells,
            section = MallSection.Cultural
        ),
        Product(
            id = "bronze_bells",
            titleRes = R.string.product_bronze_bells,
            priceYuan = 128,
            rating = 4.7f,
            reviewCount = 8,
            descriptionRes = R.string.product_desc_bronze,
            imageRes = R.drawable.product_bronze_bells,
            section = MallSection.Cross
        ),
        Product(
            id = "cross_tote_bag",
            titleRes = R.string.product_cross_tote,
            priceYuan = 79,
            rating = 4.5f,
            reviewCount = 18,
            descriptionRes = R.string.product_desc_cross_tote,
            imageRes = R.drawable.product_silk_scarf,
            section = MallSection.Cross
        ),
        Product(
            id = "cross_badge_set",
            titleRes = R.string.product_cross_badge,
            priceYuan = 36,
            rating = 4.6f,
            reviewCount = 24,
            descriptionRes = R.string.product_desc_cross_badge,
            imageRes = R.drawable.product_dunhuang,
            section = MallSection.Cross
        ),
        Product(
            id = "pipa_bookmark",
            titleRes = R.string.product_pipa_model,
            priceYuan = 25,
            rating = 4.6f,
            reviewCount = 15,
            descriptionRes = R.string.product_desc_pipa,
            imageRes = R.drawable.product_pipa_bookmark,
            section = MallSection.Instrument
        ),
        Product(
            id = "inst_score_notebook",
            titleRes = R.string.product_inst_score,
            priceYuan = 49,
            rating = 4.7f,
            reviewCount = 10,
            descriptionRes = R.string.product_desc_inst_score,
            imageRes = R.drawable.product_dunhuang,
            section = MallSection.Instrument
        ),
        Product(
            id = "inst_clip_tuner",
            titleRes = R.string.product_inst_clip_tuner,
            priceYuan = 119,
            rating = 4.8f,
            reviewCount = 33,
            descriptionRes = R.string.product_desc_inst_clip_tuner,
            imageRes = R.drawable.product_bronze_bells,
            section = MallSection.Instrument
        )
    )

    override fun products(): List<Product> = items

    override fun productById(id: String): Product? = items.find { it.id == id }

    override fun productsInSection(section: MallSection): List<Product> =
        items.filter { it.section == section }
}
