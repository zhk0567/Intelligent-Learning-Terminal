package com.guyunxinchuan.heritage.music

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.guyunxinchuan.heritage.music.CartActivity.CartItem

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onItemAction: (CartItem, Action) -> Unit,
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var isEditMode = false

    enum class Action {
        QUANTITY_CHANGE,
        SELECTION_CHANGE,
        DELETE,
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val selectCheckBox: CheckBox = itemView.findViewById(R.id.selectCheckBox)
        val productImage: ShapeableImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productTagLine: TextView = itemView.findViewById(R.id.productTagLine)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val quantityTextView: TextView = itemView.findViewById(R.id.quantityTextView)
        val decreaseButton: TextView = itemView.findViewById(R.id.decreaseButton)
        val increaseButton: TextView = itemView.findViewById(R.id.increaseButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
        val totalPriceTextView: TextView = itemView.findViewById(R.id.totalPriceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        val product = cartItem.product

        holder.productName.text = product.name
        holder.productPrice.text = "¥${String.format("%.2f", product.price)}"
        holder.productTagLine.text = "${product.category} · 演示库存 · 不支持拆单合单"

        val fallback = if (product.imageResId != 0) {
            product.imageResId
        } else {
            R.drawable.music_cover_placeholder
        }
        holder.productImage.loadCoverRemoteOrDrawable(
            StaticRemoteAssets.productListCoverRemote(product),
            fallback,
            CoverPreset.Thumb,
        )

        holder.selectCheckBox.setOnCheckedChangeListener(null)
        holder.selectCheckBox.isChecked = cartItem.selected
        holder.selectCheckBox.setOnCheckedChangeListener { _, isChecked ->
            cartItem.selected = isChecked
            onItemAction(cartItem, Action.SELECTION_CHANGE)
        }

        holder.quantityTextView.text = cartItem.quantity.toString()

        holder.decreaseButton.setOnClickListener {
            if (cartItem.quantity > 1) {
                cartItem.quantity--
                holder.quantityTextView.text = cartItem.quantity.toString()
                updateTotalPrice(holder, cartItem)
                onItemAction(cartItem, Action.QUANTITY_CHANGE)
            }
        }

        holder.increaseButton.setOnClickListener {
            cartItem.quantity++
            holder.quantityTextView.text = cartItem.quantity.toString()
            updateTotalPrice(holder, cartItem)
            onItemAction(cartItem, Action.QUANTITY_CHANGE)
        }

        holder.deleteButton.visibility = if (isEditMode) View.VISIBLE else View.GONE
        holder.deleteButton.setOnClickListener {
            onItemAction(cartItem, Action.DELETE)
        }

        updateTotalPrice(holder, cartItem)
    }

    private fun updateTotalPrice(holder: CartViewHolder, cartItem: CartItem) {
        val totalPrice = cartItem.product.price * cartItem.quantity
        holder.totalPriceTextView.text = "¥${String.format("%.2f", totalPrice)}"
    }

    override fun onViewRecycled(holder: CartViewHolder) {
        holder.productImage.cancelCoverLoad()
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = cartItems.size

    fun setEditMode(editMode: Boolean) {
        isEditMode = editMode
        notifyDataSetChanged()
    }
}
