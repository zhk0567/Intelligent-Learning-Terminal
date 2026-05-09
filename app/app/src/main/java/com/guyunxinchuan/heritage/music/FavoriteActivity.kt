package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.guyunxinchuan.heritage.music.databinding.ActivityFavoriteBinding

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var musicAdapter: ProductAdapter
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var courseAdapter: ProductAdapter
    private lateinit var productAdapter: ProductAdapter

    private var isEditMode = false
    private val selectedItems: MutableSet<String> = mutableSetOf()

    private lateinit var favoriteMusics: MutableList<Product>
    private lateinit var favoriteStories: MutableList<Story>
    private lateinit var favoriteCourses: MutableList<Product>
    private lateinit var favoriteProducts: MutableList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyRootOnly(this, binding.root, 0f)

        initFavoriteLists()

        setupViews()
        setupRecyclerViews()
        setupBatchActions()
    }

    /** 与正文目录一致：取前两篇，封面走 `/images/story/{key}.jpg`。 */
    private fun buildFavoriteStories(): MutableList<Story> {
        val tiles = listOf(R.color.story_mock_tile_1, R.color.story_mock_tile_2)
        return StoriesData.ALL.take(2).mapIndexed { i, e ->
            val hw = if (e.coverWidth > 0 && e.coverHeight > 0) {
                e.coverHeight.toFloat() / e.coverWidth.toFloat()
            } else {
                9f / 16f
            }
            Story(
                id = e.id,
                title = e.title,
                author = e.author,
                category = e.category,
                placeholderColorRes = tiles[i % tiles.size],
                aspectRatio = hw,
                imageResId = -1,
                content = e.excerpt,
                publishTime = e.publishTime,
                coverWidth = e.coverWidth,
                coverHeight = e.coverHeight,
                coverResId = e.coverResId,
                coverRemoteUrl = StaticRemoteAssets.storyCover(e.key),
                likeCount = e.likeCount,
                commentCount = e.commentCount,
                readCount = e.readCount,
                tags = e.tags,
            )
        }.toMutableList()
    }

    private fun initFavoriteLists() {
        favoriteMusics = mutableListOf(
            Product(
                "1",
                getString(R.string.fav_m1_title),
                18.0,
                4.8f,
                getString(R.string.fav_m1_desc),
                "",
                getString(R.string.fav_cat_guzheng),
                listOf(getString(R.string.fav_tag_mingqu), getString(R.string.fav_tag_chuantong)),
                imageResId = R.drawable.banner1_img,
            ),
            Product(
                "2",
                getString(R.string.fav_m2_title),
                22.0,
                4.9f,
                getString(R.string.fav_m2_desc),
                "",
                getString(R.string.fav_cat_pipa),
                listOf(getString(R.string.fav_tag_duzou), getString(R.string.fav_tag_guofeng)),
                imageResId = R.drawable.banner2_img,
            ),
        )
        favoriteStories = buildFavoriteStories()
        favoriteCourses = mutableListOf(
            Product(
                "1",
                getString(R.string.fav_c1_title),
                99.0,
                4.9f,
                getString(R.string.fav_c1_desc),
                "",
                getString(R.string.fav_cat_course),
                listOf(getString(R.string.fav_tag_rumen), getString(R.string.fav_cat_guzheng)),
                imageResId = R.drawable.banner3_img,
            ),
            Product(
                "2",
                getString(R.string.fav_c2_title),
                128.0,
                4.8f,
                getString(R.string.fav_c2_desc),
                "",
                getString(R.string.fav_cat_course),
                listOf(getString(R.string.fav_tag_jinjie), getString(R.string.fav_tag_guqin)),
                imageResId = R.drawable.banner1_img,
            ),
        )
        favoriteProducts = mutableListOf(
            Product(
                "1",
                getString(R.string.fav_p1_title),
                30.0,
                4.9f,
                getString(R.string.fav_p1_desc),
                "",
                getString(R.string.fav_cat_wenchuang),
                listOf(getString(R.string.fav_tag_cixiu), getString(R.string.fav_tag_shuqian)),
                imageResId = R.drawable.banner1_img,
                salesCount = 1200,
            ),
            Product(
                "2",
                getString(R.string.fav_p2_title),
                25.0,
                4.7f,
                getString(R.string.fav_p2_desc),
                "",
                getString(R.string.fav_cat_chaju),
                listOf(getString(R.string.fav_tag_qingci), getString(R.string.fav_cat_chaju)),
                imageResId = R.drawable.banner2_img,
                salesCount = 856,
            ),
        )
    }

    private fun setupViews() {
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.editButton.setOnClickListener {
            toggleEditMode()
        }
    }

    private fun setupRecyclerViews() {
        musicAdapter = ProductAdapter(favoriteMusics) { product ->
            navigateToMusicDetail(product)
        }
        binding.musicRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FavoriteActivity)
            adapter = musicAdapter
        }

        storyAdapter = StoryAdapter(this, favoriteStories) { story ->
            navigateToStoryDetail(story)
        }
        binding.storyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FavoriteActivity)
            adapter = storyAdapter
        }

        courseAdapter = ProductAdapter(favoriteCourses) { product ->
            navigateToCourseDetail(product)
        }
        binding.courseRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FavoriteActivity)
            adapter = courseAdapter
        }

        productAdapter = ProductAdapter(favoriteProducts) { product ->
            navigateToProductDetail(product)
        }
        binding.productRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FavoriteActivity)
            adapter = productAdapter
        }

        binding.tabLayout.addOnTabSelectedListener(object :
            com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showMusicTab()
                    1 -> showStoryTab()
                    2 -> showCourseTab()
                    3 -> showProductTab()
                }
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })

        showMusicTab()
    }

    private fun showMusicTab() {
        binding.musicRecyclerView.visibility = View.VISIBLE
        binding.storyRecyclerView.visibility = View.GONE
        binding.courseRecyclerView.visibility = View.GONE
        binding.productRecyclerView.visibility = View.GONE
        updateEmptyState()
    }

    private fun showStoryTab() {
        binding.musicRecyclerView.visibility = View.GONE
        binding.storyRecyclerView.visibility = View.VISIBLE
        binding.courseRecyclerView.visibility = View.GONE
        binding.productRecyclerView.visibility = View.GONE
        updateEmptyState()
    }

    private fun showCourseTab() {
        binding.musicRecyclerView.visibility = View.GONE
        binding.storyRecyclerView.visibility = View.GONE
        binding.courseRecyclerView.visibility = View.VISIBLE
        binding.productRecyclerView.visibility = View.GONE
        updateEmptyState()
    }

    private fun showProductTab() {
        binding.musicRecyclerView.visibility = View.GONE
        binding.storyRecyclerView.visibility = View.GONE
        binding.courseRecyclerView.visibility = View.GONE
        binding.productRecyclerView.visibility = View.VISIBLE
        updateEmptyState()
    }

    private fun updateEmptyState() {
        val currentTab = binding.tabLayout.selectedTabPosition
        val isEmpty = when (currentTab) {
            0 -> favoriteMusics.isEmpty()
            1 -> favoriteStories.isEmpty()
            2 -> favoriteCourses.isEmpty()
            3 -> favoriteProducts.isEmpty()
            else -> true
        }

        if (isEmpty) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.musicRecyclerView.visibility = View.GONE
            binding.storyRecyclerView.visibility = View.GONE
            binding.courseRecyclerView.visibility = View.GONE
            binding.productRecyclerView.visibility = View.GONE

            val titles = listOf(
                getString(R.string.fav_empty_music_title),
                getString(R.string.fav_empty_story_title),
                getString(R.string.fav_empty_course_title),
                getString(R.string.fav_empty_product_title),
            )
            val descriptions = listOf(
                getString(R.string.fav_empty_music_desc),
                getString(R.string.fav_empty_story_desc),
                getString(R.string.fav_empty_course_desc),
                getString(R.string.fav_empty_product_desc),
            )

            binding.emptyTitleTextView.text = titles[currentTab]
            binding.emptyDescriptionTextView.text = descriptions[currentTab]
        } else {
            binding.emptyStateLayout.visibility = View.GONE
        }
    }

    private fun setupBatchActions() {
        binding.selectAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectAllItems()
            } else {
                clearSelection()
            }
        }

        binding.deleteSelectedButton.setOnClickListener {
            if (selectedItems.isEmpty()) {
                UiFeedback.toast(this, getString(R.string.fav_toast_select_delete_first))
                return@setOnClickListener
            }

            android.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.fav_dialog_delete_title))
                .setMessage(getString(R.string.fav_dialog_delete_message, selectedItems.size))
                .setPositiveButton(getString(R.string.fav_dialog_delete_positive)) { _, _ ->
                    deleteSelectedItems()
                }
                .setNegativeButton(getString(R.string.fav_dialog_cancel), null)
                .show()
        }

        binding.cancelBatchButton.setOnClickListener {
            exitEditMode()
        }
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode

        if (isEditMode) {
            binding.editButton.text = getString(R.string.fav_edit_done)
            binding.batchActionBar.visibility = View.VISIBLE
            selectedItems.clear()
            binding.selectAllCheckBox.isChecked = false
            UiFeedback.toast(this, getString(R.string.fav_toast_batch_mode))
        } else {
            exitEditMode()
        }
    }

    private fun exitEditMode() {
        isEditMode = false
        binding.editButton.text = getString(R.string.fav_edit_label)
        binding.batchActionBar.visibility = View.GONE
        selectedItems.clear()
        binding.selectAllCheckBox.isChecked = false
        UiFeedback.toast(this, getString(R.string.fav_toast_exit_edit))
    }

    private fun selectAllItems() {
        val currentTab = binding.tabLayout.selectedTabPosition
        when (currentTab) {
            0 -> selectedItems.addAll(favoriteMusics.map { it.id })
            1 -> selectedItems.addAll(favoriteStories.map { it.id.toString() })
            2 -> selectedItems.addAll(favoriteCourses.map { it.id })
            3 -> selectedItems.addAll(favoriteProducts.map { it.id })
        }
        UiFeedback.toast(this, getString(R.string.fav_toast_select_all, selectedItems.size))
    }

    private fun clearSelection() {
        selectedItems.clear()
    }

    private fun deleteSelectedItems() {
        val currentTab = binding.tabLayout.selectedTabPosition
        val deletedCount = selectedItems.size
        when (currentTab) {
            0 -> favoriteMusics.removeAll { it.id in selectedItems }
            1 -> favoriteStories.removeAll { it.id.toString() in selectedItems }
            2 -> favoriteCourses.removeAll { it.id in selectedItems }
            3 -> favoriteProducts.removeAll { it.id in selectedItems }
        }

        selectedItems.clear()
        binding.selectAllCheckBox.isChecked = false

        when (currentTab) {
            0 -> musicAdapter.notifyDataSetChanged()
            1 -> storyAdapter.notifyDataSetChanged()
            2 -> courseAdapter.notifyDataSetChanged()
            3 -> productAdapter.notifyDataSetChanged()
        }

        updateEmptyState()
        UiFeedback.toast(this, getString(R.string.fav_toast_deleted, deletedCount))
    }

    private fun navigateToMusicDetail(product: Product) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("music_id", product.id)
        intent.putExtra("music_title", product.name)
        startActivity(intent)
    }

    private fun navigateToStoryDetail(story: Story) {
        val idStr = story.id.toString()
        val entry = StoriesData.ALL.find { it.id == idStr }
        val body = entry?.body ?: story.content
        val excerpt = entry?.excerpt?.trim().orEmpty()
        startActivity(Intent(this, StoryDetailActivity::class.java).apply {
            putExtra(StoryDetailPayload.Extras.ID, idStr)
            putExtra(StoryDetailPayload.Extras.TITLE, story.title)
            putExtra(StoryDetailPayload.Extras.AUTHOR, story.author)
            putExtra(StoryDetailPayload.Extras.PUBLISHED_AT, story.publishTime)
            putExtra(StoryDetailPayload.Extras.BODY, body)
            if (excerpt.isNotEmpty() && excerpt != body.trim()) {
                putExtra(StoryDetailPayload.Extras.SUMMARY, excerpt)
            }
            putExtra(StoryDetailPayload.Extras.COVER, story.coverResId)
            putExtra(StoryDetailPayload.Extras.CATEGORY, story.category)
            putExtra(StoryDetailPayload.Extras.TAGS, story.tags.joinToString("|||"))
            putExtra(StoryDetailPayload.Extras.READ_COUNT, story.readCount)
            putExtra(StoryDetailPayload.Extras.LIKE_COUNT, story.likeCount)
            putExtra(StoryDetailPayload.Extras.COMMENT_COUNT, story.commentCount)
        })
    }

    private fun navigateToCourseDetail(product: Product) {
        UiFeedback.toast(this, getString(R.string.fav_toast_course_open, product.name))
    }

    private fun navigateToProductDetail(product: Product) {
        val priceText = getString(R.string.fav_price_format, String.format("%.2f", product.price))
        val inCatalog = ShopCatalog.allProducts.any { it.id == product.id }
        val wcIdx = ShopCatalog.catalogIndex(product)
        val intent = Intent(this, ShopDetailActivity::class.java).apply {
            putExtra("product_name", product.name)
            putExtra("product_price", priceText)
            putExtra("product_rating", "${product.rating}")
            putExtra("product_desc", product.description)
            if (product.imageResId != 0) putExtra("product_image", product.imageResId)
            if (product.detailHeroResId != 0) putExtra("product_hero", product.detailHeroResId)
            putExtra("product_sales", product.salesCount)
            if (inCatalog) {
                StaticRemoteAssets.wcCover(wcIdx)?.let { putExtra("product_image_remote", it) }
                StaticRemoteAssets.wcMock(wcIdx)?.let { putExtra("product_hero_remote", it) }
            } else {
                val banner = StaticRemoteAssets.remoteBannerMatchingLocal(product.imageResId)
                banner?.let {
                    putExtra("product_image_remote", it)
                    putExtra("product_hero_remote", it)
                }
            }
        }
        startActivity(intent)
    }

    private fun clearAllFavorites() {
        android.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.fav_clear_title))
            .setMessage(getString(R.string.fav_clear_message))
            .setPositiveButton(getString(R.string.fav_clear_positive)) { _, _ ->
                UiFeedback.toast(this, getString(R.string.fav_clear_toast_sample))
                updateEmptyState()
            }
            .setNegativeButton(getString(R.string.fav_dialog_cancel), null)
            .show()
    }
}
