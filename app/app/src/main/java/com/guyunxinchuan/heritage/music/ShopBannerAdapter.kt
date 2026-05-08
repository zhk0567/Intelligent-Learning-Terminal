package com.guyunxinchuan.heritage.music

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.imageview.ShapeableImageView

data class ShopBannerSlide(
    val title: String,
    val line: String,
    val imageResId: Int,
)

class ShopBannerAdapter(
    private val slides: List<ShopBannerSlide>,
    private val onSlideClick: (position: Int) -> Unit,
) : RecyclerView.Adapter<ShopBannerAdapter.BannerViewHolder>() {

    private val pageCount: Int
        get() {
            val n = slides.size.coerceAtLeast(1)
            return n * LOOP_FACTOR
        }

    fun realSlideIndex(adapterPosition: Int): Int {
        if (slides.isEmpty()) return 0
        val p = adapterPosition % slides.size
        return (p + slides.size) % slides.size
    }

    override fun getItemCount(): Int = if (slides.isEmpty()) 0 else pageCount

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop_banner_slide, parent, false)
        return BannerViewHolder(v)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val idx = realSlideIndex(position)
        holder.bind(slides[idx], idx, onSlideClick)
    }

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ShapeableImageView = itemView.findViewById(R.id.bannerImage)
        private val title: TextView = itemView.findViewById(R.id.bannerTitle)
        private val line: TextView = itemView.findViewById(R.id.bannerLine)

        fun bind(slide: ShopBannerSlide, position: Int, onSlideClick: (Int) -> Unit) {
            val dm = itemView.resources.displayMetrics
            val wPx = (itemView.width.takeIf { it > 0 } ?: dm.widthPixels).coerceIn(320, 1080)
            val hPx = (156 * dm.density).toInt().coerceIn(200, 800)
            image.load(slide.imageResId) {
                size(wPx, hPx)
                crossfade(false)
                allowRgb565(true)
            }
            title.text = slide.title
            line.text = slide.line
            itemView.setOnClickListener { onSlideClick(position) }
        }
    }

    /** 与中间对齐的起始页，对应第 0 张幻灯片 */
    fun loopStartIndex(): Int {
        if (slides.isEmpty()) return 0
        val n = slides.size
        val mid = itemCount / 2
        return mid - (mid % n)
    }

    companion object {
        const val LOOP_FACTOR = 240
    }
}
