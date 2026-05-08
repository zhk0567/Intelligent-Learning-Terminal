package com.guyunxinchuan.heritage.music

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productRating: TextView = itemView.findViewById(R.id.productRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        if (product.imageResId != 0) {
            holder.productImage.loadCover(product.imageResId, CoverPreset.Card)
        } else {
            holder.productImage.loadCover(R.drawable.music_cover_placeholder, CoverPreset.Card)
        }
        holder.productName.text = product.name
        holder.productPrice.text = "¥${String.format("%.2f", product.price)}"
        holder.productRating.text = String.format("%.1f", product.rating)

        holder.itemView.setOnClickListener {
            onItemClick(product)
        }
    }

    override fun onViewRecycled(holder: ProductViewHolder) {
        holder.productImage.cancelCoverLoad()
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = products.size
}
