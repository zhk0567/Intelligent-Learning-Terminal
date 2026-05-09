package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.guyunxinchuan.heritage.music.databinding.ActivitySearchResultBinding

class SearchResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultBinding
    private lateinit var searchAdapter: SearchAdapter

    private val allResults = mutableListOf<SearchResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyRootOnly(this, binding.root, 0f)

        setupViews()
        performSearch()
    }

    private fun setupViews() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.searchInput.setText(intent.getStringExtra("keyword") ?: "")
        binding.searchInput.setSelection(binding.searchInput.text.length)

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        binding.btnSearch.setOnClickListener {
            performSearch()
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchAdapter(allResults) { result ->
            navigateToDetail(result)
        }

        binding.resultRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchResultActivity)
            adapter = searchAdapter
        }
    }

    private fun performSearch() {
        val keyword = binding.searchInput.text.toString().trim()
        if (keyword.isEmpty()) {
            UiFeedback.toast(this, "请输入搜索关键词")
            return
        }

        binding.loadingProgress.visibility = View.VISIBLE
        binding.emptyStateLayout.visibility = View.GONE
        binding.resultRecyclerView.visibility = View.GONE

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            generateMockResults(keyword)

            binding.loadingProgress.visibility = View.GONE

            if (allResults.isEmpty()) {
                showEmptyState()
            } else {
                showResults()
            }
        }, 500)
    }

    private fun generateMockResults(keyword: String) {
        allResults.clear()

        allResults.add(
            SearchResult(
                type = SearchResultType.MUSIC,
                title = "$keyword · 精选合辑",
                subtitle = "国风雅集 · 3:45",
                imageResId = R.drawable.banner1_img,
                id = "music_1"
            )
        )

        allResults.add(
            SearchResult(
                type = SearchResultType.STORY,
                title = "走近「$keyword」的非遗故事",
                subtitle = "非遗故事集 · 2 小时前",
                imageResId = R.drawable.banner2_img,
                id = "story_henan_zhuizi",
            )
        )

        allResults.add(
            SearchResult(
                type = SearchResultType.COURSE,
                title = "$keyword 入门精讲",
                subtitle = "12 节课 · 已学 300+",
                imageResId = R.drawable.banner3_img,
                id = "course_1"
            )
        )

        val kw = keyword.trim()
        ShopCatalog.allProducts.filter { productMatches(it, kw) }.forEach { p ->
            val priceFmt = "¥${String.format("%.2f", p.price)}"
            allResults.add(
                SearchResult(
                    type = SearchResultType.PRODUCT,
                    title = p.name,
                    subtitle = "$priceFmt · ${p.rating}分 · 已售${p.salesCount}",
                    imageResId = p.imageResId,
                    id = p.id,
                    detailHeroResId = p.detailHeroResId,
                    productRating = "${p.rating}",
                    productDesc = p.description,
                    productSales = p.salesCount,
                    productPriceFormatted = priceFmt,
                )
            )
        }

        searchAdapter.notifyDataSetChanged()
    }

    private fun productMatches(p: Product, kw: String): Boolean {
        if (kw.isEmpty()) return false
        if (p.name.contains(kw, ignoreCase = true)) return true
        if (p.category.contains(kw, ignoreCase = true)) return true
        if (p.tags.any { it.contains(kw, ignoreCase = true) }) return true
        if (p.description.contains(kw, ignoreCase = true)) return true
        return false
    }

    private fun showEmptyState() {
        binding.emptyStateLayout.visibility = View.VISIBLE
        binding.resultRecyclerView.visibility = View.GONE
    }

    private fun showResults() {
        binding.emptyStateLayout.visibility = View.GONE
        binding.resultRecyclerView.visibility = View.VISIBLE
    }

    private fun navigateToDetail(result: SearchResult) {
        when (result.type) {
            SearchResultType.MUSIC -> {
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("music_id", result.id)
                intent.putExtra("music_title", result.title)
                startActivity(intent)
            }
            SearchResultType.STORY -> {
                val entry = StoriesData.ALL.find { it.id == result.id }
                startActivity(Intent(this, StoryDetailActivity::class.java).apply {
                    putExtra(StoryDetailPayload.Extras.ID, result.id)
                    if (entry != null) {
                        putExtra(StoryDetailPayload.Extras.TITLE, entry.title)
                        putExtra(StoryDetailPayload.Extras.AUTHOR, entry.author)
                        putExtra(StoryDetailPayload.Extras.PUBLISHED_AT, entry.publishTime)
                        putExtra(StoryDetailPayload.Extras.BODY, entry.body)
                        putExtra(StoryDetailPayload.Extras.SUMMARY, entry.excerpt)
                        putExtra(StoryDetailPayload.Extras.COVER, entry.coverResId)
                        putExtra(StoryDetailPayload.Extras.CATEGORY, entry.category)
                        putExtra(StoryDetailPayload.Extras.TAGS, entry.tags.joinToString("|||"))
                        putExtra(StoryDetailPayload.Extras.READ_COUNT, entry.readCount)
                        putExtra(StoryDetailPayload.Extras.LIKE_COUNT, entry.likeCount)
                        putExtra(StoryDetailPayload.Extras.COMMENT_COUNT, entry.commentCount)
                    } else {
                        putExtra(StoryDetailPayload.Extras.TITLE, result.title)
                    }
                })
            }
            SearchResultType.COURSE -> {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("title", result.title)
                intent.putExtra("content_title", result.title)
                intent.putExtra(
                    "content_text",
                    "本课程围绕「${result.title}」展开，包含基础知识、技法演练与作业点评，适合零基础学员系统入门。",
                )
                intent.putExtra("action_button_text", "立即学习")
                startActivity(intent)
            }
            SearchResultType.PRODUCT -> {
                val intent = Intent(this, ShopDetailActivity::class.java).apply {
                    putExtra("product_name", result.title)
                    putExtra("product_price", result.productPriceFormatted ?: "¥0")
                    putExtra("product_rating", result.productRating ?: "4.9")
                    if (result.imageResId != 0) putExtra("product_image", result.imageResId)
                    if (result.detailHeroResId != 0) putExtra("product_hero", result.detailHeroResId)
                    result.productDesc?.let { putExtra("product_desc", it) }
                    if (result.productSales >= 0) putExtra("product_sales", result.productSales)
                    ShopCatalog.allProducts.firstOrNull { it.id == result.id }?.let { p ->
                        val wcIdx = ShopCatalog.catalogIndex(p)
                        StaticRemoteAssets.wcCover(wcIdx)?.let { putExtra("product_image_remote", it) }
                        StaticRemoteAssets.wcMock(wcIdx)?.let { putExtra("product_hero_remote", it) }
                    }
                }
                startActivity(intent)
            }
        }
    }

    enum class SearchResultType {
        MUSIC, STORY, COURSE, PRODUCT
    }

    data class SearchResult(
        val type: SearchResultType,
        val title: String,
        val subtitle: String,
        val imageResId: Int,
        val id: String,
        /** 商品详情顶图（样机），非商品类型为 0 */
        val detailHeroResId: Int = 0,
        val productRating: String? = null,
        val productDesc: String? = null,
        val productSales: Int = -1,
        val productPriceFormatted: String? = null,
    )
}
