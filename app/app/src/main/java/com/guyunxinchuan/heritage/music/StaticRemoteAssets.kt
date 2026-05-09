package com.guyunxinchuan.heritage.music

import androidx.annotation.DrawableRes

/**
 * 与 Web `VITE_STATIC_ORIGIN`、小程序 `STATIC_ORIGIN` 一致：当 [BuildConfig.STATIC_ASSET_ORIGIN] 非空时，
 * 图/封面从 ECS/CDN 拉取，路径与 `web/public` 下文件一致；为空则各界面仍用 `drawable`。
 */
object StaticRemoteAssets {

    private fun base(): String? =
        BuildConfig.STATIC_ASSET_ORIGIN.trim().trimEnd('/').takeIf { it.isNotEmpty() }

    /** @param path 须以 `/` 开头，如 `/images/foo.jpg` */
    fun url(path: String): String? {
        val b = base() ?: return null
        val p = if (path.startsWith("/")) path else "/$path"
        return b + p
    }

    fun storyCover(key: String): String? = url("/images/story/$key.jpg")

    /** 乐库首页轮播 `home_swiper_{1..3}.jpg`，index0 为 0..2 循环 */
    fun homeSwiper(index0: Int): String? {
        val i = (index0 % 3) + 1
        return url("/images/home_swiper_$i.jpg")
    }

    /** 商城顶栏 `shop_swiper_{1..3}.jpg` */
    fun shopSwiper(index0: Int): String? {
        val i = (index0 % 3) + 1
        return url("/images/shop_swiper_$i.jpg")
    }

    /** `banner1_img.jpg` … `banner3_img.jpg`，slot 任意整数取模 3 */
    fun bannerImg(slot: Int): String? {
        val n = (slot % 3) + 1
        return url("/images/banner${n}_img.jpg")
    }

    private fun pad2(n: Int): String = if (n < 10) "0$n" else "$n"

    fun dailyHotCover(index1: Int): String? =
        url("/images/daily_hot/daily_hot_cover_${pad2(index1)}.jpg")

    fun dailySelectCover(index1: Int): String? =
        url("/images/daily_select/daily_select_cover_${pad2(index1)}.jpg")

    fun dailyGuessCover(index1: Int): String? =
        url("/images/daily_guess/daily_guess_cover_${pad2(index1)}.jpg")

    /** 文创列表封面 `wc_cover_01.jpg` …，`listIndex` 为商品在列表中下标 0..n */
    fun wcCover(listIndex: Int): String? {
        val n = (listIndex % 20) + 1
        return url("/images/shop/wc_cover_${pad2(n)}.jpg")
    }

    /** 详情样机 `wc_mock_01.jpg` … */
    fun wcMock(listIndex: Int): String? {
        val n = (listIndex % 17) + 1
        return url("/images/shop/wc_mock_${pad2(n)}.jpg")
    }

    fun basicLessonCover(lessonIndex0: Int): String? {
        val n = lessonIndex0 + 1
        return url("/images/basic_lesson/basic_lesson_cover_${pad2(n)}.jpg")
    }

    /** [PlayerSyncState] 曲目顺序与封面 drawable 一致 */
    fun playerTrackCover(trackIndex: Int): String? = when (trackIndex) {
        in 0..4 -> bannerImg(trackIndex)
        5 -> dailyHotCover(1)
        6 -> dailyHotCover(2)
        7 -> dailyHotCover(3)
        8 -> dailyHotCover(4)
        9 -> dailySelectCover(1)
        10 -> dailySelectCover(2)
        11 -> dailySelectCover(3)
        12 -> dailyGuessCover(1)
        13 -> dailyGuessCover(2)
        14 -> dailyGuessCover(3)
        else -> null
    }

    /** 与 Web `playerStore.ts` 中 `TRACKS[].audioSrc` 路径一致；0–4 为 `audio/classic/classic_0N.mp3`。 */
    fun playerTrackAudio(trackIndex: Int): String? = when (trackIndex) {
        0 -> url("/audio/classic/classic_01.mp3")
        1 -> url("/audio/classic/classic_02.mp3")
        2 -> url("/audio/classic/classic_03.mp3")
        3 -> url("/audio/classic/classic_04.mp3")
        4 -> url("/audio/classic/classic_05.mp3")
        5 -> url("/audio/daily_hot/daily_hot_01.mp3")
        6 -> url("/audio/daily_hot/daily_hot_02.mp3")
        7 -> url("/audio/daily_hot/daily_hot_03.mp3")
        8 -> url("/audio/daily_hot/daily_hot_04.mp3")
        9 -> url("/audio/daily_select/daily_select_01.mp3")
        10 -> url("/audio/daily_select/daily_select_02.mp3")
        11 -> url("/audio/daily_select/daily_select_03.mp3")
        12 -> url("/audio/daily_guess/daily_guess_01.mp3")
        13 -> url("/audio/daily_guess/daily_guess_02.mp3")
        14 -> url("/audio/daily_guess/daily_guess_03.mp3")
        else -> null
    }

    /** 与乐库轮播/详情里 `banner*_img` drawable 对应的静态 URL（仅这三张有线上同名资源）。 */
    fun remoteBannerMatchingLocal(@DrawableRes coverResId: Int): String? = when (coverResId) {
        R.drawable.banner1_img -> bannerImg(0)
        R.drawable.banner2_img -> bannerImg(1)
        R.drawable.banner3_img -> bannerImg(2)
        else -> null
    }

    /**
     * 列表/购物车封面：在 [ShopCatalog] 中的商品用 `wc_cover_*` URL；
     * 否则若 [Product.imageResId] 为三张轮播图之一则走 [remoteBannerMatchingLocal]。
     */
    fun productListCoverRemote(product: Product): String? {
        val idx = ShopCatalog.allProducts.indexOfFirst { it.id == product.id }
        if (idx >= 0) return wcCover(idx)
        return remoteBannerMatchingLocal(product.imageResId)
    }
}
