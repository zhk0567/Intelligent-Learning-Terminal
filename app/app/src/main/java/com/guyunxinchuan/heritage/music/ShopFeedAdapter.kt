package com.guyunxinchuan.heritage.music

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.imageview.ShapeableImageView

class ShopFeedAdapter(
    private var products: List<Product>,
    private var footerMode: FooterMode,
    private val onProductClick: (Product) -> Unit,
    private val onAddToCart: (Product) -> Unit,
    private val onHotSeeAllClick: () -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class FooterMode {
        NONE, LOADING, END, EMPTY
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_PRODUCT = 1
        private const val TYPE_FOOTER_LOADING = 2
        private const val TYPE_FOOTER_END = 3
        private const val TYPE_FOOTER_EMPTY = 4
    }

    fun submit(products: List<Product>, footerMode: FooterMode) {
        this.products = products
        this.footerMode = footerMode
        notifyDataSetChanged()
    }

    fun gridSpanSize(position: Int): Int =
        if (getItemViewType(position) == TYPE_PRODUCT) 1 else 2

    override fun getItemCount(): Int {
        val footerSlots = when (footerMode) {
            FooterMode.NONE -> 0
            else -> 1
        }
        if (products.isEmpty() && footerMode == FooterMode.EMPTY) {
            return 1 + footerSlots
        }
        return 1 + products.size + footerSlots
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return TYPE_HEADER
        if (products.isEmpty()) {
            return TYPE_FOOTER_EMPTY
        }
        val body = position - 1
        if (body < products.size) return TYPE_PRODUCT
        return when (footerMode) {
            FooterMode.LOADING -> TYPE_FOOTER_LOADING
            FooterMode.END -> TYPE_FOOTER_END
            FooterMode.EMPTY -> TYPE_FOOTER_EMPTY
            FooterMode.NONE -> TYPE_FOOTER_END
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderVH(inflater.inflate(R.layout.item_shop_header, parent, false))
            TYPE_PRODUCT -> ProductVH(inflater.inflate(R.layout.item_shop_product, parent, false))
            TYPE_FOOTER_LOADING -> SimpleVH(inflater.inflate(R.layout.item_shop_footer_loading, parent, false))
            TYPE_FOOTER_END -> SimpleVH(inflater.inflate(R.layout.item_shop_footer_end, parent, false))
            TYPE_FOOTER_EMPTY -> SimpleVH(inflater.inflate(R.layout.item_shop_footer_empty, parent, false))
            else -> error("unknown type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderVH -> {
                holder.hotSeeAll.setOnClickListener { onHotSeeAllClick() }
                // 轮播由 ShopActivity 在列表绑定后注入
            }
            is ProductVH -> {
                val p = products[position - 1]
                holder.bind(p, onProductClick, onAddToCart)
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is ProductVH) holder.recycleImage()
        super.onViewRecycled(holder)
    }

    class HeaderVH(view: View) : RecyclerView.ViewHolder(view) {
        val bannerPager: ViewPager2 = view.findViewById(R.id.shopHeaderBannerPager)
        val bannerDots: LinearLayout = view.findViewById(R.id.shopHeaderBannerDots)
        val hotSeeAll: View = view.findViewById(R.id.shopHotSeeAll)
    }

    class ProductVH(private val view: View) : RecyclerView.ViewHolder(view) {
        private val image: ShapeableImageView = view.findViewById(R.id.shopProductImage)
        private val name: TextView = view.findViewById(R.id.shopProductName)
        private val price: TextView = view.findViewById(R.id.shopProductPrice)
        private val meta: TextView = view.findViewById(R.id.shopProductMeta)
        private val newTag: TextView = view.findViewById(R.id.shopProductNewTag)
        private val addCart: ImageButton = view.findViewById(R.id.shopAddCartButton)

        fun recycleImage() {
            image.cancelCoverLoad()
        }

        fun bind(
            p: Product,
            onProductClick: (Product) -> Unit,
            onAddToCart: (Product) -> Unit,
        ) {
            val fallback = if (p.imageResId != 0) p.imageResId else R.drawable.music_cover_placeholder
            image.loadCoverRemoteOrDrawable(
                StaticRemoteAssets.productListCoverRemote(p),
                fallback,
                CoverPreset.Card,
            )
            name.text = p.name
            price.text = "¥${String.format("%.2f", p.price)}"
            meta.text = "${String.format("%.1f", p.rating)}分 · ${p.salesCount}人收货"
            newTag.visibility = if (p.isNew) View.VISIBLE else View.GONE

            view.setOnClickListener { onProductClick(p) }
            addCart.setOnClickListener {
                onAddToCart(p)
            }
        }
    }

    class SimpleVH(view: View) : RecyclerView.ViewHolder(view)
}
