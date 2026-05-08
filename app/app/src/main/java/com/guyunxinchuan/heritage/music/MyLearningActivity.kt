package com.guyunxinchuan.heritage.music

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.guyunxinchuan.heritage.music.databinding.ActivityMyLearningBinding

class MyLearningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyLearningBinding
    private lateinit var learningAdapter: MyLearningAdapter

    private val learningList = mutableListOf(
        MyLearning("1", "古琴入门教程", 75, "学习中"),
        MyLearning("2", "二胡基础教学", 100, "已完成"),
        MyLearning("3", "琵琶进阶技巧", 30, "学习中")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyLearningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyRootOnly(this, binding.root, 0f)

        setupViews()
        setupRecyclerView()
    }

    private fun setupViews() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.tabLayout.addOnTabSelectedListener(object :
            com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> filterByStatus("学习中")
                    1 -> filterByStatus("已完成")
                }
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        learningAdapter = MyLearningAdapter(
            learningList.filter { it.status == "学习中" }
        ) { learning ->
            UiFeedback.toast(this, "继续学习: ${learning.courseName}")
        }

        binding.learningRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MyLearningActivity)
            adapter = learningAdapter
        }

        updateEmptyState()
    }

    private fun filterByStatus(status: String) {
        val filteredList = learningList.filter { it.status == status }
        learningAdapter.updateList(filteredList)
        updateEmptyState()
    }

    private fun updateEmptyState() {
        if (learningAdapter.itemCount == 0) {
            binding.emptyView.visibility = View.VISIBLE
            binding.learningRecyclerView.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.learningRecyclerView.visibility = View.VISIBLE
        }
    }

    data class MyLearning(
        val id: String,
        val courseName: String,
        val progress: Int,
        val status: String
    )
}
