package com.guyunxinchuan.heritage.music

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(
    private val historyList: List<HistoryItem>,
    private val onItemClick: (HistoryItem) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.historyItemImage)
        val itemTitle: TextView = view.findViewById(R.id.historyItemTitle)
        val itemType: TextView = view.findViewById(R.id.historyItemType)
        val itemTime: TextView = view.findViewById(R.id.historyItemTime)
        val deleteButton: ImageView = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]

        holder.itemImage.loadCoverRemoteOrDrawable(
            StaticRemoteAssets.remoteBannerMatchingLocal(item.imageResId),
            item.imageResId,
            CoverPreset.Thumb,
        )
        holder.itemTitle.text = item.title
        holder.itemType.text = item.type
        holder.itemTime.text = item.browseTime

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        holder.deleteButton.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                val context = holder.itemView.context
                if (context is HistoryActivity) {
                    context.deleteItemAt(pos)
                }
            }
        }
    }

    override fun onViewRecycled(holder: HistoryViewHolder) {
        holder.itemImage.cancelCoverLoad()
        super.onViewRecycled(holder)
    }

    override fun getItemCount() = historyList.size
}
