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
        HistoryItem("1", "????????", "??", R.drawable.banner2_img, "2024-01-15 14:30"),
        HistoryItem("2", "???????????", "??", R.drawable.banner1_img, "2024-01-15 13:20"),
        HistoryItem("3", "??????", "??", R.drawable.banner3_img, "2024-01-15 12:10"),
        HistoryItem("4", "????????????", "??", R.drawable.banner2_img, "2024-01-14 16:45"),
        HistoryItem("5", "???????", "??", R.drawable.banner1_img, "2024-01-14 15:30"),
        HistoryItem("6", "AI??????????", "??", R.drawable.banner3_img, "2024-01-14 10:20")
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
            .setTitle("??????")
            .setMessage("???????????????")
            .setPositiveButton("??") { _, _ ->
                clearAllHistory()
            }
            .setNegativeButton("??", null)
            .show()
    }

    private fun clearAllHistory() {
        historyList.clear()
        historyAdapter.notifyDataSetChanged()
        updateEmptyState()
        UiFeedback.toast(this, "???????")
    }

    private fun deleteItem(position: Int) {
        if (position >= 0 && position < historyList.size) {
            historyList.removeAt(position)
            historyAdapter.notifyItemRemoved(position)
            updateEmptyState()
            UiFeedback.toast(this, "???")
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
        // 历史数据中 type 字面量已退化为同一占位字符串，无法再做差异化路由，
        // 这里统一跳到作品详情即可，避免无意义的重复 when 分支。
        startActivity(Intent(this, WorkDetailActivity::class.java).apply {
            putExtra("work_title", item.title)
            putExtra("work_author", "???")
        })
    }

    fun deleteItemAt(position: Int) {
        deleteItem(position)
    }
}
