package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.guyunxinchuan.heritage.music.databinding.ActivityMyWorksBinding

class MyWorksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyWorksBinding
    private lateinit var worksAdapter: MyWorksAdapter

    private val worksList = mutableListOf<MyWork>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyWorksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyRootOnly(this, binding.root, 0f)

        refreshWorksFromStore()
        setupViews()
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        refreshWorksFromStore()
        if (::worksAdapter.isInitialized) {
            worksAdapter.notifyDataSetChanged()
            updateEmptyState()
        }
    }

    private fun setupViews() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.createButton.setOnClickListener {
            startActivity(Intent(this, PublishWorkActivity::class.java))
        }
    }

    private fun refreshWorksFromStore() {
        worksList.clear()
        worksList.addAll(UserPublishedContent.myWorksDisplayList())
    }

    private fun setupRecyclerView() {
        worksAdapter = MyWorksAdapter(
            worksList,
            onEdit = { work ->
                UiFeedback.toast(this, "编辑: ${work.title}")
            },
            onDelete = { work ->
                showDeleteDialog(work)
            },
            onViewData = { work ->
                showWorkData(work)
            }
        )

        binding.worksRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MyWorksActivity)
            adapter = worksAdapter
        }

        updateEmptyState()
    }

    private fun showDeleteDialog(work: MyWork) {
        AlertDialog.Builder(this)
            .setTitle("删除作品")
            .setMessage("确定要删除《${work.title}》吗？")
            .setPositiveButton("确定") { _, _ ->
                deleteWork(work)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun deleteWork(work: MyWork) {
        UserPublishedContent.removeWork(work.id)
        refreshWorksFromStore()
        worksAdapter.notifyDataSetChanged()
        updateEmptyState()
        UiFeedback.toast(this, "作品已删除")
    }

    private fun showWorkData(work: MyWork) {
        val message = """
            作品：${work.title}
            点赞：${work.likes}
            评论：${work.comments}
            浏览：${work.views}
            状态：${work.status}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("作品数据")
            .setMessage(message)
            .setPositiveButton("确定", null)
            .show()
    }

    private fun updateEmptyState() {
        if (worksList.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.worksRecyclerView.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.worksRecyclerView.visibility = View.VISIBLE
        }
    }

    data class MyWork(
        val id: String,
        val title: String,
        val publishDate: String,
        val likes: Int,
        val comments: Int,
        val views: Int,
        val status: String
    )
}
