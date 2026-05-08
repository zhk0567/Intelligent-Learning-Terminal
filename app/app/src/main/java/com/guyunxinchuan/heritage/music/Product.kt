package com.guyunxinchuan.heritage.music

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val rating: Float,
    val description: String,
    val imageUrl: String,
    val category: String,
    val tags: List<String>,
    /** 列表封面（`data/文创/图片` → `wc_cover_*`）。 */
    val imageResId: Int = 0,
    /** 详情页主图（`data/文创/样机` → `wc_mock_*`）。 */
    val detailHeroResId: Int = 0,
    /** 商城筛选：heritage / instrument / aroma / collab / sale，空表示非商城分类数据 */
    val shopCategoryId: String = "",
    val originalPrice: Double? = null,
    val salesCount: Int = 0,
    val isNew: Boolean = false,
)
