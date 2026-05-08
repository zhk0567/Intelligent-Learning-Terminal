package com.guyunxinchuan.heritage.music

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyWorksAdapter(
    private val worksList: List<MyWorksActivity.MyWork>,
    private val onEdit: (MyWorksActivity.MyWork) -> Unit,
    private val onDelete: (MyWorksActivity.MyWork) -> Unit,
    private val onViewData: (MyWorksActivity.MyWork) -> Unit
) : RecyclerView.Adapter<MyWorksAdapter.WorksViewHolder>() {

    class WorksViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.workTitle)
        val dateText: TextView = view.findViewById(R.id.workDate)
        val statusBadge: TextView = view.findViewById(R.id.workStatus)
        val likesText: TextView = view.findViewById(R.id.workLikes)
        val commentsText: TextView = view.findViewById(R.id.workComments)
        val viewsText: TextView = view.findViewById(R.id.workViews)
        val editButton: ImageView = view.findViewById(R.id.editButton)
        val deleteButton: ImageView = view.findViewById(R.id.deleteButton)
        val dataButton: ImageView = view.findViewById(R.id.dataButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorksViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_work, parent, false)
        return WorksViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorksViewHolder, position: Int) {
        val work = worksList[position]
        
        holder.titleText.text = work.title
        holder.dateText.text = work.publishDate
        holder.statusBadge.text = work.status
        holder.likesText.text = "❤ ${work.likes}"
        holder.commentsText.text = "💬 ${work.comments}"
        holder.viewsText.text = "👁 ${work.views}"
        
        // 根据状态设置不同颜色
        when (work.status) {
            "已发布" -> holder.statusBadge.setBackgroundResource(R.drawable.tag_bg_selected)
            "草稿" -> holder.statusBadge.setBackgroundResource(R.drawable.tag_bg_normal)
        }
        
        holder.editButton.setOnClickListener { onEdit(work) }
        holder.deleteButton.setOnClickListener { onDelete(work) }
        holder.dataButton.setOnClickListener { onViewData(work) }
    }

    override fun getItemCount() = worksList.size
}
