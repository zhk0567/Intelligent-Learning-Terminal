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
import java.util.Locale

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
            buildSearchResults(keyword)

            binding.loadingProgress.visibility = View.GONE

            if (allResults.isEmpty()) {
                showEmptyState(keyword)
            } else {
                showResults()
            }
        }, 500)
    }

    private fun buildSearchResults(keyword: String) {
        allResults.clear()
        val kw = keyword.trim()
        if (kw.isEmpty()) {
            searchAdapter.notifyDataSetChanged()
            return
        }

        PlayerSyncState.tracks.forEachIndexed { index, t ->
            if (trackMatches(t, kw)) {
                allResults.add(
                    SearchResult(
                        type = SearchResultType.MUSIC,
                        title = t.title,
                        subtitle = "${t.artist} · ${t.album} · ${formatDurationMs(t.durationMs)}",
                        imageResId = t.coverResId,
                        id = index.toString(),
                        thumbRemoteUrl = t.coverRemoteUrl,
                    ),
                )
            }
        }

        StoriesData.ALL.forEach { e ->
            if (storyMatches(e, kw)) {
                allResults.add(
                    SearchResult(
                        type = SearchResultType.STORY,
                        title = e.title,
                        subtitle = e.author,
                        imageResId = e.coverResId,
                        id = e.id,
                        thumbRemoteUrl = StaticRemoteAssets.storyCover(e.key),
                    ),
                )
            }
        }

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

    private fun trackMatches(t: PlayerSyncState.SyncTrack, kw: String): Boolean =
        t.title.contains(kw, ignoreCase = true) ||
            t.artist.contains(kw, ignoreCase = true) ||
            t.album.contains(kw, ignoreCase = true)

    private fun storyMatches(e: StoryEntry, kw: String): Boolean =
        e.title.contains(kw, ignoreCase = true) ||
            e.author.contains(kw, ignoreCase = true) ||
            e.excerpt.contains(kw, ignoreCase = true) ||
            e.tags.any { it.contains(kw, ignoreCase = true) }

    private fun formatDurationMs(ms: Int): String {
        val totalSec = (ms / 1000).coerceAtLeast(0)
        val m = totalSec / 60
        val s = totalSec % 60
        return String.format(Locale.CHINA, "%d:%02d", m, s)
    }

    private fun productMatches(p: Product, kw: String): Boolean {
        if (kw.isEmpty()) return false
        if (p.name.contains(kw, ignoreCase = true)) return true
        if (p.category.contains(kw, ignoreCase = true)) return true
        if (p.tags.any { it.contains(kw, ignoreCase = true) }) return true
        if (p.description.contains(kw, ignoreCase = true)) return true
        return false
    }

    private fun showEmptyState(keyword: String) {
        binding.emptyStateLayout.visibility = View.VISIBLE
        binding.resultRecyclerView.visibility = View.GONE
        val displayKw = keyword.trim().ifEmpty { "…" }
        binding.emptyText.text = getString(R.string.search_result_empty_title, displayKw)
        binding.emptySubtext.text = getString(R.string.search_result_empty_hint)
    }

    private fun showResults() {
        binding.emptyStateLayout.visibility = View.GONE
        binding.resultRecyclerView.visibility = View.VISIBLE
    }

    private fun navigateToDetail(result: SearchResult) {
        when (result.type) {
            SearchResultType.MUSIC -> {
                val idx = result.id.toIntOrNull()?.coerceIn(0, PlayerSyncState.tracks.lastIndex) ?: 0
                val t = PlayerSyncState.tracks[idx]
                PlayerSyncState.setTrack(idx, t.durationMs)
                PlayerSyncState.currentPositionMs = 0
                PlayerSyncState.updatePlayingState(false)
                startActivity(Intent(this, PlayerActivity::class.java))
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
        /** 列表缩略图优先使用的远程 URL（曲目/故事封面等）；为空则按 [imageResId] 走 banner 映射或本地图。 */
        val thumbRemoteUrl: String? = null,
        /** 商品详情顶图（样机），非商品类型为 0 */
        val detailHeroResId: Int = 0,
        val productRating: String? = null,
        val productDesc: String? = null,
        val productSales: Int = -1,
        val productPriceFormatted: String? = null,
    )
}
