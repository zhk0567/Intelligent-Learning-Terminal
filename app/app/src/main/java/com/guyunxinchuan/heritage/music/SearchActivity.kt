package com.guyunxinchuan.heritage.music

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SearchActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var btnClear: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var btnSearch: TextView
    private lateinit var btnClearHistory: ImageView

    private val hotTagIds = intArrayOf(
        R.id.tag_t1, R.id.tag_t2, R.id.tag_t3, R.id.tag_t4,
        R.id.tag_t5, R.id.tag_t6, R.id.tag_t7, R.id.tag_t8,
        R.id.tag_t9, R.id.tag_t10, R.id.tag_t11, R.id.tag_t12
    )

    /** 每组 12 个：文案 + 是否热门样式 */
    private val hotSearchBatches: List<List<Pair<String, Boolean>>> = listOf(
        listOf(
            Pair("🔥 敦煌乐舞", true),
            Pair("非遗音乐库", false),
            Pair("古筝入门", false),
            Pair("昆曲牡丹亭", false),
            Pair("🔥 编钟演奏", true),
            Pair("丝路音乐", false),
            Pair("苗族芦笙", false),
            Pair("水墨音韵", false),
            Pair("🔥 古琴流水", true),
            Pair("纳西古乐", false),
            Pair("蒙古长调", false),
            Pair("皮影戏", false)
        ),
        listOf(
            Pair("🔥 南音琵琶", true),
            Pair("十二木卡姆", false),
            Pair("侗族大歌", false),
            Pair("马头琴曲", false),
            Pair("🔥 京剧唱腔", true),
            Pair("潮州音乐", false),
            Pair("江南丝竹", false),
            Pair("西安鼓乐", false),
            Pair("🔥 长调民歌", true),
            Pair("彝族月琴", false),
            Pair("藏族扎念", false),
            Pair("花鼓戏", false)
        ),
        listOf(
            Pair("🔥 古琴减字谱", true),
            Pair("工尺谱入门", false),
            Pair("曾侯乙编钟", false),
            Pair("敦煌琵琶谱", false),
            Pair("🔥 AI 作曲", true),
            Pair("电子国风", false),
            Pair("国乐录音棚", false),
            Pair("非遗纪录片", false),
            Pair("🔥 乐舞复原", true),
            Pair("唐乐研究", false),
            Pair("宋韵雅乐", false),
            Pair("戏曲打击乐", false)
        )
    )

    private var hotBatchIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)

        MallWindowInsets.applyToContentRoot(this, 0f)

        searchInput = findViewById(R.id.search_input)
        btnClear = findViewById(R.id.btn_clear)
        btnBack = findViewById(R.id.btn_back)
        btnSearch = findViewById(R.id.btn_search)
        btnClearHistory = findViewById(R.id.btn_clear_history)

        setupSearchBar()
        setupButtons()
        setupTagClicks()

        searchInput.requestFocus()
        searchInput.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT)
        }, 150)
    }

    private fun setupSearchBar() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnClear.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        })
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchInput.text.toString().trim())
                true
            } else {
                false
            }
        }
    }

    private fun setupButtons() {
        btnBack.setOnClickListener { finish() }
        btnClear.setOnClickListener { searchInput.text.clear(); searchInput.requestFocus() }
        btnSearch.setOnClickListener { performSearch(searchInput.text.toString().trim()) }
        btnClearHistory.setOnClickListener {
            UiFeedback.toast(this, "搜索历史已清空")
        }
        findViewById<LinearLayout>(R.id.btn_refresh_hot)?.setOnClickListener {
            hotBatchIndex = (hotBatchIndex + 1) % hotSearchBatches.size
            applyHotSearchBatch(hotSearchBatches[hotBatchIndex])
            setupTagClicks()
        }
    }

    private fun applyHotSearchBatch(batch: List<Pair<String, Boolean>>) {
        val hotColor = Color.WHITE
        val normalColor = ContextCompat.getColor(this, R.color.text_primary)
        batch.forEachIndexed { index, pair ->
            if (index >= hotTagIds.size) return@forEachIndexed
            val tv = findViewById<TextView>(hotTagIds[index])
            val (text, hot) = pair
            tv.text = text
            if (hot) {
                tv.setBackgroundResource(R.drawable.search_tag_hot)
                tv.setTextColor(hotColor)
            } else {
                tv.setBackgroundResource(R.drawable.search_tag_normal)
                tv.setTextColor(normalColor)
            }
        }
    }

    private fun setupTagClicks() {
        setTagClicksInView(window.decorView)
    }

    private fun setTagClicksInView(view: View) {
        if (view is TextView
            && view.id != R.id.btn_back
            && view.id != R.id.btn_search
            && view.id != R.id.btn_clear
            && view.id != R.id.search_input
            && view.background != null
            && view.isClickable) {
            view.setOnClickListener {
                val raw = (it as TextView).text.toString()
                val keyword = raw.replace(Regex("^[^\\u4e00-\\u9fa5a-zA-Z0-9]+"), "").trim()
                if (keyword.isNotEmpty()) {
                    searchInput.setText(keyword)
                    searchInput.setSelection(keyword.length)
                    performSearch(keyword)
                }
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) setTagClicksInView(view.getChildAt(i))
        }
    }

    private fun performSearch(keyword: String) {
        if (keyword.isEmpty()) {
            UiFeedback.toast(this, "请输入搜索内容")
            return
        }
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchInput.windowToken, 0)

        val intent = Intent(this, SearchResultActivity::class.java).apply {
            putExtra("keyword", keyword)
        }
        startActivity(intent)
    }
}
