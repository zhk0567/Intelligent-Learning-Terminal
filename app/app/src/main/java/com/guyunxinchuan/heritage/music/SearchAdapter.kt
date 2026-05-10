package com.guyunxinchuan.heritage.music

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchAdapter(
    private val results: List<SearchResultActivity.SearchResult>,
    private val onItemClick: (SearchResultActivity.SearchResult) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.result_image)
        val typeText: TextView = view.findViewById(R.id.result_type)
        val titleText: TextView = view.findViewById(R.id.result_title)
        val subtitleText: TextView = view.findViewById(R.id.result_subtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val result = results[position]

        val remote = result.thumbRemoteUrl?.trim()?.takeIf { it.isNotEmpty() }
            ?: StaticRemoteAssets.remoteBannerMatchingLocal(result.imageResId)
        holder.image.loadCoverRemoteOrDrawable(
            remote,
            result.imageResId,
            CoverPreset.Thumb,
        )

        val typeText = when (result.type) {
            SearchResultActivity.SearchResultType.MUSIC -> "音乐"
            SearchResultActivity.SearchResultType.STORY -> "故事"
            SearchResultActivity.SearchResultType.COURSE -> "课程"
            SearchResultActivity.SearchResultType.PRODUCT -> "商品"
        }
        holder.typeText.text = typeText

        holder.titleText.text = result.title
        holder.subtitleText.text = result.subtitle

        holder.itemView.setOnClickListener {
            onItemClick(result)
        }
    }

    override fun onViewRecycled(holder: SearchViewHolder) {
        holder.image.cancelCoverLoad()
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = results.size
}
