package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.guyunxinchuan.heritage.music.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter

    private val historyList = mutableListOf(
        HistoryItem("1", "古琴名曲：广陵散全本赏析", "音乐", R.drawable.banner2_img, "2024-01-15 14:30"),
        HistoryItem("2", "祥云纹刺绣书签（礼盒）", "商品", R.drawable.banner1_img, "2024-01-15 13:20"),
        HistoryItem("3", "走近河南坠子的传承故事", "故事", R.drawable.banner3_img, "2024-01-15 12:10"),
        HistoryItem("4", "古筝入门：认弦与基本指法", "课程", R.drawable.banner2_img, "2024-01-14 16:45"),
        HistoryItem("5", "青瓷茶盏 · 非遗联名款", "商品", R.drawable.banner1_img, "2024-01-14 15:30"),
        HistoryItem("6", "AI 与传统乐器的跨界融合", "作品", R.drawable.banner3_img, "2024-01-14 10:20")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyRootOnly(this, binding.root, 0f)

        setupViews()
        setupRecyclerView()
    }

    private fun setupViews() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.clearAllButton.setOnClickListener {
            showClearConfirmDialog()
        }
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(historyList) { item ->
            navigateToDetail(item)
        }

        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = historyAdapter
        }

        updateEmptyState()
    }

    private fun showClearConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("清空浏览历史")
            .setMessage("确定要清空全部浏览历史吗？此操作不可恢复。")
            .setPositiveButton("清空") { _, _ ->
                clearAllHistory()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun clearAllHistory() {
        historyList.clear()
        historyAdapter.notifyDataSetChanged()
        updateEmptyState()
        UiFeedback.toast(this, "浏览历史已清空")
    }

    private fun deleteItem(position: Int) {
        if (position >= 0 && position < historyList.size) {
            historyList.removeAt(position)
            historyAdapter.notifyItemRemoved(position)
            updateEmptyState()
            UiFeedback.toast(this, "已删除")
        }
    }

    private fun updateEmptyState() {
        if (historyList.isEmpty()) {
            binding.emptyView.visibility = android.view.View.VISIBLE
            binding.historyRecyclerView.visibility = android.view.View.GONE
        } else {
            binding.emptyView.visibility = android.view.View.GONE
            binding.historyRecyclerView.visibility = android.view.View.VISIBLE
        }
    }

    private fun navigateToDetail(item: HistoryItem) {
        startActivity(Intent(this, WorkDetailActivity::class.java).apply {
            putExtra("work_title", item.title)
            putExtra("work_author", "非遗文创")
        })
    }

    fun deleteItemAt(position: Int) {
        deleteItem(position)
    }
}
