package com.guyunxinchuan.heritage.music

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyLearningAdapter(
    private var learningList: List<MyLearningActivity.MyLearning>,
    private val onItemClick: (MyLearningActivity.MyLearning) -> Unit
) : RecyclerView.Adapter<MyLearningAdapter.LearningViewHolder>() {

    class LearningViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val courseName: TextView = view.findViewById(R.id.courseName)
        val progressText: TextView = view.findViewById(R.id.progressText)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        val statusBadge: TextView = view.findViewById(R.id.statusBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LearningViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_learning, parent, false)
        return LearningViewHolder(view)
    }

    override fun onBindViewHolder(holder: LearningViewHolder, position: Int) {
        val learning = learningList[position]
        
        holder.courseName.text = learning.courseName
        holder.progressText.text = "${learning.progress}%"
        holder.progressBar.progress = learning.progress
        holder.statusBadge.text = learning.status
        
        when (learning.status) {
            "学习中" -> holder.statusBadge.setBackgroundResource(R.drawable.tag_bg_selected)
            "已完成" -> holder.statusBadge.setBackgroundResource(R.drawable.tag_bg_normal)
        }
        
        holder.itemView.setOnClickListener { onItemClick(learning) }
    }

    override fun getItemCount() = learningList.size
    
    fun updateList(newList: List<MyLearningActivity.MyLearning>) {
        learningList = newList
        notifyDataSetChanged()
    }
}
