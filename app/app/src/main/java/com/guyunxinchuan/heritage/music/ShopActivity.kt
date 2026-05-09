package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class ShopActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var recyclerView: RecyclerView
    private lateinit var skeletonOverlay: FrameLayout
    private lateinit var cartBadge: TextView
    private lateinit var feedAdapter: ShopFeedAdapter
    private var bannerPager: ViewPager2? = null
    private var bannerDots: LinearLayout? = null
    private var shopBannerPageCallback: ViewPager2.OnPageChangeCallback? = null
    private var bannerPagerRegisteredOn: ViewPager2? = null

    private var selectedCategoryId: String = ShopCategoryIds.ALL
    private var sortByHotSales: Boolean = false
    private var loadedCount: Int = PAGE_SIZE
    private var isLoadingMore: Boolean = false
    private var isBannerPaused = false
    private val bannerAdvanceRunnable = object : Runnable {
        override fun run() {
            val pager = bannerPager ?: return
            if (isBannerPaused) return
            val n = pager.adapter?.itemCount ?: 0
            if (n <= 1) return
            val next = (pager.currentItem + 1) % n
            pager.setCurrentItem(next, false)
            handler.postDelayed(this, BANNER_INTERVAL_MS)
        }
    }

    private val mockAddresses = listOf(
        "张三 · 138****8000\n北京市朝阳区建国路88号",
        "李四 · 139****9000\n上海市浦东新区陆家嘴环路1000号",
        "王五 · 137****7000\n广州市天河区珠江新城",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        recyclerView = findViewById(R.id.productRecyclerView)
        skeletonOverlay = findViewById(R.id.shopSkeletonOverlay)
        cartBadge = findViewById(R.id.cartBadge)

        feedAdapter = ShopFeedAdapter(
            products = emptyList(),
            footerMode = ShopFeedAdapter.FooterMode.NONE,
            onProductClick = { openProductDetail(it) },
            onAddToCart = { addToCartFromCard(it) },
            onHotSeeAllClick = {
                sortByHotSales = true
                selectCategory(ShopCategoryIds.ALL)
            },
        )

        val gridLayoutManager = GridLayoutManager(this, 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int = feedAdapter.gridSpanSize(position)
            }
        }
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = feedAdapter
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(16)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                val lm = rv.layoutManager as? GridLayoutManager ?: return
                val last = lm.findLastVisibleItemPosition()
                val total = feedAdapter.itemCount
                if (last >= total - 3) tryLoadMore()
            }
        })

        bindIconInteractions(findViewById(R.id.addressButton))
        bindIconInteractions(findViewById(R.id.cartButton))

        findViewById<ImageButton>(R.id.addressButton).setOnClickListener { showAddressBottomSheet() }
        findViewById<ImageButton>(R.id.cartButton).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        val bottomNav = findViewById<LinearLayout>(R.id.bottom_navigation)
        BottomNavigationManager(this, "ShopActivity").setupNavigation(bottomNav)

        MallWindowInsets.applyToActivity(
            this,
            findViewById(R.id.shopPageRoot),
            findViewById(R.id.shopTopInsetHost),
            bottomNav,
            recyclerView,
            8f,
        )

        showInitialSkeletonThenBind()
    }

    override fun onResume() {
        super.onResume()
        refreshCartBadge()
        isBannerPaused = false
        scheduleBannerAdvance()
    }

    override fun onPause() {
        handler.removeCallbacks(bannerAdvanceRunnable)
        isBannerPaused = true
        super.onPause()
    }

    private fun bindShopBannerFromListHeader() {
        val vh = recyclerView.findViewHolderForAdapterPosition(0) as? ShopFeedAdapter.HeaderVH ?: return
        val pager = vh.bannerPager
        val dots = vh.bannerDots
        if (bannerPager === pager && pager.adapter != null) return

        shopBannerPageCallback?.let { cb ->
            bannerPagerRegisteredOn?.unregisterOnPageChangeCallback(cb)
        }
        bannerPagerRegisteredOn = pager
        bannerPager = pager
        bannerDots = dots

        val slides = shopBannerSlides()
        val adapter = ShopBannerAdapter(slides) { pos ->
            when (pos) {
                0 -> {
                    sortByHotSales = false
                    selectCategory(ShopCategoryIds.HERITAGE)
                }
                1 -> {
                    sortByHotSales = true
                    selectCategory(ShopCategoryIds.INSTRUMENT)
                }
                2 -> {
                    sortByHotSales = false
                    selectCategory(ShopCategoryIds.AROMA)
                }
            }
            UiFeedback.toast(this, "已切换分类")
        }
        pager.adapter = adapter
        pager.setCurrentItem(adapter.loopStartIndex(), false)
        pager.offscreenPageLimit = 1

        buildBannerDots(slides.size)

        val cb = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val ad = pager.adapter as? ShopBannerAdapter ?: return
                updateBannerDots(ad.realSlideIndex(position))
            }

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager2.SCROLL_STATE_DRAGGING -> {
                        handler.removeCallbacks(bannerAdvanceRunnable)
                    }
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        normalizeShopBannerPosition()
                        scheduleBannerAdvance()
                    }
                }
            }
        }
        shopBannerPageCallback = cb
        pager.registerOnPageChangeCallback(cb)
        scheduleBannerAdvance()
    }

    /** 与小程序/Web `shopSwiper*`、`shop_swiper_*.jpg` 一致；源图 `data/图片/商城页轮播图`。 */
    private fun shopBannerSlides() = listOf(
        ShopBannerSlide(
            title = "季中精选 · 非遗好物",
            line = "博物馆联名与工坊严选，点按进入「非遗文创」分类。",
            imageResId = R.drawable.shop_swiper_1,
            imageRemoteUrl = StaticRemoteAssets.shopSwiper(0),
        ),
        ShopBannerSlide(
            title = "乐器周边热销",
            line = "琴弦拨片、模型摆件与乐谱周边，按销量浏览。",
            imageResId = R.drawable.shop_swiper_2,
            imageRemoteUrl = StaticRemoteAssets.shopSwiper(1),
        ),
        ShopBannerSlide(
            title = "香薰雅物专区",
            line = "线香、蜡烛与香器，静室一隅的仪式感。",
            imageResId = R.drawable.shop_swiper_3,
            imageRemoteUrl = StaticRemoteAssets.shopSwiper(2),
        ),
    )

    private fun normalizeShopBannerPosition() {
        val pager = bannerPager ?: return
        val adapter = pager.adapter as? ShopBannerAdapter ?: return
        val slides = shopBannerSlides()
        if (slides.isEmpty()) return
        val n = adapter.itemCount
        val p = pager.currentItem
        val buf = slides.size * 20
        when {
            p < buf -> pager.setCurrentItem(p + slides.size * 80, false)
            p >= n - buf -> pager.setCurrentItem(p - slides.size * 80, false)
        }
    }

    private fun buildBannerDots(count: Int) {
        val bannerDots = this.bannerDots ?: return
        bannerDots.removeAllViews()
        val d = resources.displayMetrics.density
        val small = (6 * d).toInt()
        repeat(count) { index ->
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(small, small).apply {
                    if (index > 0) marginStart = (6 * d).toInt()
                }
                setBackgroundResource(R.drawable.shop_banner_dot_normal)
            }
            bannerDots.addView(dot)
        }
        updateBannerDots(0)
    }

    private fun updateBannerDots(current: Int) {
        val bannerDots = this.bannerDots ?: return
        val d = resources.displayMetrics.density
        val big = (8 * d).toInt()
        val sm = (6 * d).toInt()
        for (i in 0 until bannerDots.childCount) {
            val v = bannerDots.getChildAt(i)
            val lp = v.layoutParams as LinearLayout.LayoutParams
            if (i == current) {
                lp.width = big
                lp.height = big
                v.setBackgroundResource(R.drawable.shop_banner_dot_selected)
            } else {
                lp.width = sm
                lp.height = sm
                v.setBackgroundResource(R.drawable.shop_banner_dot_normal)
            }
            v.layoutParams = lp
        }
    }

    private fun scheduleBannerAdvance() {
        handler.removeCallbacks(bannerAdvanceRunnable)
        if (isBannerPaused || bannerPager == null) return
        val n = bannerPager?.adapter?.itemCount ?: 0
        if (n <= 1) return
        handler.postDelayed(bannerAdvanceRunnable, BANNER_INTERVAL_MS)
    }

    private fun showInitialSkeletonThenBind() {
        // 不再首屏把列表 alpha 置 0 + 200ms 延迟，否则切换到底栏商城时中间会有一截暗场/像黑屏闪一下。
        // 数据立即提交，骨架层保持隐藏。
        skeletonOverlay.isVisible = false
        recyclerView.alpha = 1f
        selectCategory(selectedCategoryId)
    }

    private fun selectCategory(id: String) {
        selectedCategoryId = id
        val cap = ShopCatalog.filterByCategory(ShopCatalog.allProducts, selectedCategoryId).let { list ->
            val sorted = if (sortByHotSales) list.sortedByDescending { it.salesCount } else list
            sorted.size
        }
        loadedCount = PAGE_SIZE.coerceAtMost(cap)
        isLoadingMore = false
        recomputeListAndSubmit()
        recyclerView.scrollToPosition(0)
    }

    private fun fullFilteredList(): List<Product> {
        var list = ShopCatalog.filterByCategory(ShopCatalog.allProducts, selectedCategoryId)
        if (sortByHotSales) {
            list = list.sortedByDescending { it.salesCount }
        }
        return list
    }

    private fun recomputeListAndSubmit() {
        val full = fullFilteredList()
        val visible = full.take(loadedCount.coerceAtMost(full.size))
        val hasMore = visible.size < full.size

        val footer = when {
            full.isEmpty() -> ShopFeedAdapter.FooterMode.EMPTY
            !hasMore && visible.isNotEmpty() -> ShopFeedAdapter.FooterMode.END
            hasMore && isLoadingMore -> ShopFeedAdapter.FooterMode.LOADING
            else -> ShopFeedAdapter.FooterMode.NONE
        }
        feedAdapter.submit(visible, footer)
        recyclerView.post {
            bindShopBannerFromListHeader()
        }
    }

    private fun tryLoadMore() {
        if (isLoadingMore) return
        val full = fullFilteredList()
        if (loadedCount >= full.size) return
        isLoadingMore = true
        recomputeListAndSubmit()
        handler.postDelayed({
            loadedCount = (loadedCount + PAGE_SIZE).coerceAtMost(full.size)
            isLoadingMore = false
            recomputeListAndSubmit()
        }, 520)
    }

    private fun openProductDetail(product: Product) {
        val wcIdx = ShopCatalog.catalogIndex(product)
        startActivity(Intent(this, ShopDetailActivity::class.java).apply {
            putExtra("product_name", product.name)
            putExtra("product_price", "¥${String.format("%.2f", product.price)}")
            putExtra("product_rating", "${product.rating}")
            putExtra("product_image", product.imageResId)
            putExtra("product_hero", product.detailHeroResId)
            putExtra("product_desc", product.description)
            putExtra("product_sales", product.salesCount)
            StaticRemoteAssets.wcCover(wcIdx)?.let { putExtra("product_image_remote", it) }
            StaticRemoteAssets.wcMock(wcIdx)?.let { putExtra("product_hero_remote", it) }
        })
    }

    private fun addToCartFromCard(product: Product) {
        ShopCartStore.addItems(this, 1)
        refreshCartBadge()
        UiFeedback.toast(this, "已加入购物车")
    }

    private fun refreshCartBadge() {
        val n = ShopCartStore.getPendingCount(this)
        if (n <= 0) {
            cartBadge.isVisible = false
        } else {
            cartBadge.isVisible = true
            cartBadge.text = if (n > 99) "99+" else n.toString()
        }
    }

    private fun showAddressBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val content = layoutInflater.inflate(R.layout.bottom_sheet_shop_address, null, false)
        val list = content.findViewById<LinearLayout>(R.id.addressSheetList)
        mockAddresses.forEach { line ->
            val row = TextView(this).apply {
                text = line
                setTextColor(ContextCompat.getColor(this@ShopActivity, R.color.text_hint))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setPadding(
                    (16 * resources.displayMetrics.density).toInt(),
                    (14 * resources.displayMetrics.density).toInt(),
                    (16 * resources.displayMetrics.density).toInt(),
                    (14 * resources.displayMetrics.density).toInt(),
                )
                background = ContextCompat.getDrawable(this@ShopActivity, R.drawable.shop_category_chip_normal)
                setOnClickListener {
                    UiFeedback.toast(this@ShopActivity, "已选择该地址")
                    dialog.dismiss()
                }
            }
            val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.topMargin = (8 * resources.displayMetrics.density).toInt()
            list.addView(row, lp)
        }
        content.findViewById<TextView>(R.id.addressSheetManage).setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, AddressActivity::class.java))
        }
        dialog.setContentView(content)
        dialog.show()
    }

    private fun bindIconInteractions(button: ImageButton) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().cancel()
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(70).start()
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
                }
            }
            false
        }
    }

    companion object {
        private const val PAGE_SIZE = 6
        private const val BANNER_INTERVAL_MS = 4_200L
    }
}
