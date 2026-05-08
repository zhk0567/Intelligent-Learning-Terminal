package com.guyunxinchuan.heritage.music

import android.content.res.ColorStateList
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.guyunxinchuan.heritage.music.databinding.ItemOrderBinding

class OrderAdapter(
    private var orders: List<OrderActivity.Order>,
    private val onActionClick: (OrderActivity.Order, Action) -> Unit,
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    enum class Action {
        VIEW_DETAIL,
        PAY_NOW,
        CANCEL_ORDER,
        CONFIRM_RECEIPT,
        REORDER,
    }

    private enum class BtnStyle { OUTLINE, PRIMARY_TEAL, DANGER }

    class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {

        private fun dp(v: Int): Int = (v * binding.root.resources.displayMetrics.density).toInt()

        fun bind(order: OrderActivity.Order, onActionClick: (OrderActivity.Order, Action) -> Unit) {
            binding.orderNumberTextView.text = "订单号：${order.orderNumber}"
            binding.orderTimeTextView.text = order.createTime
            binding.totalAmountTextView.text = "¥${String.format("%.2f", order.totalAmount)}"
            binding.shippingAddressTextView.text = order.shippingAddress

            when (order.status) {
                OrderActivity.OrderStatus.PENDING_PAYMENT -> {
                    binding.statusTextView.text = "待支付"
                    binding.statusTextView.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.ancient_red),
                    )
                    fillActions(
                        order,
                        onActionClick,
                        listOf(
                            Triple("查看详情", Action.VIEW_DETAIL, BtnStyle.OUTLINE),
                            Triple("取消订单", Action.CANCEL_ORDER, BtnStyle.OUTLINE),
                            Triple("立即支付", Action.PAY_NOW, BtnStyle.DANGER),
                        ),
                    )
                }
                OrderActivity.OrderStatus.PENDING_SHIPMENT -> {
                    binding.statusTextView.text = "待发货"
                    binding.statusTextView.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.neon_teal_dim),
                    )
                    fillActions(
                        order,
                        onActionClick,
                        listOf(Triple("查看详情", Action.VIEW_DETAIL, BtnStyle.OUTLINE)),
                    )
                }
                OrderActivity.OrderStatus.SHIPPED -> {
                    binding.statusTextView.text = "已发货"
                    binding.statusTextView.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.neon_teal),
                    )
                    fillActions(
                        order,
                        onActionClick,
                        listOf(
                            Triple("查看详情", Action.VIEW_DETAIL, BtnStyle.OUTLINE),
                            Triple("确认收货", Action.CONFIRM_RECEIPT, BtnStyle.PRIMARY_TEAL),
                        ),
                    )
                }
                OrderActivity.OrderStatus.DELIVERED -> {
                    binding.statusTextView.text = "已完成"
                    binding.statusTextView.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.jade),
                    )
                    fillActions(
                        order,
                        onActionClick,
                        listOf(
                            Triple("查看详情", Action.VIEW_DETAIL, BtnStyle.OUTLINE),
                            Triple("再次购买", Action.REORDER, BtnStyle.PRIMARY_TEAL),
                        ),
                    )
                }
            }

            setupProductList(order.items)
            binding.receiverInfoTextView.text = "${order.receiverName}  ${order.receiverPhone}"
        }

        private fun fillActions(
            order: OrderActivity.Order,
            onActionClick: (OrderActivity.Order, Action) -> Unit,
            actions: List<Triple<String, Action, BtnStyle>>,
        ) {
            binding.actionButtonContainer.removeAllViews()
            val ctx = binding.root.context
            val gap = dp(6)
            actions.forEachIndexed { index, (label, action, style) ->
                val btn = createActionButton(ctx, label, style).apply {
                    setOnClickListener { onActionClick(order, action) }
                }
                val lp = if (actions.size >= 2) {
                    LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                        if (index > 0) marginStart = gap
                    }
                } else {
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                    )
                }
                binding.actionButtonContainer.addView(btn, lp)
            }
        }

        private fun createActionButton(ctx: android.content.Context, label: String, style: BtnStyle): MaterialButton {
            val corner = dp(8)
            return when (style) {
                BtnStyle.OUTLINE -> MaterialButton(ctx, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                    text = label
                    textSize = 12f
                    minHeight = dp(36)
                    minWidth = 0
                    insetTop = 0
                    insetBottom = 0
                    setPaddingRelative(dp(6), 0, dp(6), 0)
                    cornerRadius = corner
                    strokeWidth = dp(1)
                    strokeColor = ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.divider_light))
                    setTextColor(ContextCompat.getColor(ctx, R.color.mall_meta_readable))
                    maxLines = 1
                    isSingleLine = true
                    ellipsize = TextUtils.TruncateAt.END
                    isAllCaps = false
                }
                BtnStyle.PRIMARY_TEAL -> MaterialButton(ctx, null, com.google.android.material.R.attr.materialButtonStyle).apply {
                    text = label
                    textSize = 12f
                    minHeight = dp(36)
                    minWidth = 0
                    insetTop = 0
                    insetBottom = 0
                    setPaddingRelative(dp(6), 0, dp(6), 0)
                    cornerRadius = corner
                    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.neon_teal))
                    setTextColor(ContextCompat.getColor(ctx, R.color.black))
                    maxLines = 1
                    isSingleLine = true
                    ellipsize = TextUtils.TruncateAt.END
                    isAllCaps = false
                }
                BtnStyle.DANGER -> MaterialButton(ctx, null, com.google.android.material.R.attr.materialButtonStyle).apply {
                    text = label
                    textSize = 12f
                    minHeight = dp(36)
                    minWidth = 0
                    insetTop = 0
                    insetBottom = 0
                    setPaddingRelative(dp(6), 0, dp(6), 0)
                    cornerRadius = corner
                    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.ancient_red))
                    setTextColor(ContextCompat.getColor(ctx, R.color.white))
                    maxLines = 1
                    isSingleLine = true
                    ellipsize = TextUtils.TruncateAt.END
                    isAllCaps = false
                }
            }
        }

        private fun setupProductList(items: List<OrderActivity.OrderItem>) {
            binding.productContainer.removeAllViews()

            items.forEachIndexed { index, item ->
                val productView = LayoutInflater.from(binding.root.context)
                    .inflate(R.layout.item_order_product, binding.productContainer, false)

                val productNameTextView = productView.findViewById<TextView>(R.id.productNameTextView)
                val productQuantityTextView = productView.findViewById<TextView>(R.id.productQuantityTextView)
                val productPriceTextView = productView.findViewById<TextView>(R.id.productPriceTextView)

                productNameTextView.text = item.product.name
                productQuantityTextView.text = "×${item.quantity}"
                productPriceTextView.text = "¥${String.format("%.2f", item.price)}"

                binding.productContainer.addView(productView)

                if (index < items.size - 1) {
                    val divider = View(binding.root.context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            1,
                        )
                        setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.divider_color))
                    }
                    binding.productContainer.addView(divider)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position], onActionClick)
    }

    override fun getItemCount(): Int = orders.size

    fun updateOrders(newOrders: List<OrderActivity.Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
