package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.guyunxinchuan.heritage.music.databinding.ActivityOrderBinding

class OrderActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityOrderBinding
    private lateinit var orderAdapter: OrderAdapter
    
    // 模拟订单数据
    private val orders = mutableListOf(
        Order(
            id = "202404220001",
            orderNumber = "202404220001",
            status = OrderStatus.DELIVERED,
            totalAmount = 3998.0,
            createTime = "2024-04-22 10:30:00",
            items = listOf(
                OrderItem(
                    product = Product(
                        id = "1",
                        name = "古琴 - 仲尼式",
                        price = 2999.0,
                        rating = 4.8f,
                        description = "传统仲尼式古琴，手工制作",
                        imageUrl = "",
                        category = "乐器",
                        tags = listOf("古琴", "传统", "手工")
                    ),
                    quantity = 1,
                    price = 2999.0
                ),
                OrderItem(
                    product = Product(
                        id = "2",
                        name = "二胡 - 专业级",
                        price = 899.0,
                        rating = 4.7f,
                        description = "专业演奏级二胡，音色优美",
                        imageUrl = "",
                        category = "乐器",
                        tags = listOf("二胡", "专业", "演奏级")
                    ),
                    quantity = 1,
                    price = 899.0
                )
            ),
            shippingAddress = "北京市朝阳区建国路88号",
            receiverName = "张三",
            receiverPhone = "13800138000"
        ),
        Order(
            id = "202404210002",
            orderNumber = "202404210002",
            status = OrderStatus.PENDING_PAYMENT,
            totalAmount = 198.0,
            createTime = "2024-04-21 15:45:00",
            items = listOf(
                OrderItem(
                    product = Product(
                        id = "3",
                        name = "琵琶教材 - 入门到精通",
                        price = 99.0,
                        rating = 4.9f,
                        description = "琵琶演奏教材，适合初学者",
                        imageUrl = "",
                        category = "教材",
                        tags = listOf("琵琶", "教材", "教学")
                    ),
                    quantity = 2,
                    price = 198.0
                )
            ),
            shippingAddress = "上海市浦东新区陆家嘴环路1000号",
            receiverName = "李四",
            receiverPhone = "13900139000"
        ),
        Order(
            id = "202404200003",
            orderNumber = "202404200003",
            status = OrderStatus.SHIPPED,
            totalAmount = 128.0,
            createTime = "2024-04-20 09:15:00",
            items = listOf(
                OrderItem(
                    product = Product(
                        id = "4",
                        name = "蜀绣丝巾",
                        price = 128.0,
                        rating = 4.9f,
                        description = "精美蜀绣丝巾，手工刺绣",
                        imageUrl = "",
                        category = "工艺品",
                        tags = listOf("蜀绣", "丝巾", "手工")
                    ),
                    quantity = 1,
                    price = 128.0
                )
            ),
            shippingAddress = "广州市天河区珠江新城",
            receiverName = "王五",
            receiverPhone = "13700137000"
        ),
        Order(
            id = "202404190004",
            orderNumber = "202404190004",
            status = OrderStatus.DELIVERED,
            totalAmount = 68.0,
            createTime = "2024-04-19 14:20:00",
            items = listOf(
                OrderItem(
                    product = Product(
                        id = "5",
                        name = "青花瓷香薰蜡烛",
                        price = 68.0,
                        rating = 4.8f,
                        description = "青花瓷风格香薰蜡烛",
                        imageUrl = "",
                        category = "家居",
                        tags = listOf("青花瓷", "香薰", "蜡烛")
                    ),
                    quantity = 1,
                    price = 68.0
                )
            ),
            shippingAddress = "深圳市南山区科技园",
            receiverName = "赵六",
            receiverPhone = "13600136000"
        )
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyToActivity(
            this,
            binding.orderRoot,
            binding.orderTopInsetHost,
            bottomBar = null,
            scrollOrListBottomInset = binding.orderRecyclerView,
            scrollBottomPaddingDp = 12f,
        )

        setupViews()
        setupRecyclerView()
        updateOrderCount()
        updateFilterButtons(OrderFilter.ALL)
    }
    
    private fun setupViews() {
        // 隐藏ActionBar(使用自定义顶部栏)
        supportActionBar?.hide()
        
        // 设置返回按钮
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        
        // 设置筛选按钮点击事件
        binding.allOrdersButton.setOnClickListener {
            filterOrders(OrderFilter.ALL)
        }
        
        binding.pendingPaymentButton.setOnClickListener {
            filterOrders(OrderFilter.PENDING_PAYMENT)
        }
        
        binding.pendingShipmentButton.setOnClickListener {
            filterOrders(OrderFilter.PENDING_SHIPMENT)
        }
        
        binding.shippedButton.setOnClickListener {
            filterOrders(OrderFilter.SHIPPED)
        }
        
        binding.deliveredButton.setOnClickListener {
            filterOrders(OrderFilter.DELIVERED)
        }
        
        // 设置继续购物按钮
        binding.continueShoppingButton.setOnClickListener {
            continueShopping()
        }
        
        // 更新空状态
        updateEmptyState()
    }
    
    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(orders) { order, action ->
            when (action) {
                OrderAdapter.Action.VIEW_DETAIL -> viewOrderDetail(order)
                OrderAdapter.Action.PAY_NOW -> payOrder(order)
                OrderAdapter.Action.CONFIRM_RECEIPT -> confirmReceipt(order)
                OrderAdapter.Action.CANCEL_ORDER -> cancelOrder(order)
                OrderAdapter.Action.REORDER -> reorder(order)
            }
        }
        
        binding.orderRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrderActivity)
            adapter = orderAdapter
        }
    }
    
    private fun filterOrders(filter: OrderFilter) {
        val filteredOrders = when (filter) {
            OrderFilter.ALL -> orders
            OrderFilter.PENDING_PAYMENT -> orders.filter { it.status == OrderStatus.PENDING_PAYMENT }
            OrderFilter.PENDING_SHIPMENT -> orders.filter { it.status == OrderStatus.PENDING_SHIPMENT }
            OrderFilter.SHIPPED -> orders.filter { it.status == OrderStatus.SHIPPED }
            OrderFilter.DELIVERED -> orders.filter { it.status == OrderStatus.DELIVERED }
        }
        
        orderAdapter.updateOrders(filteredOrders)
        updateOrderCount()
        updateEmptyState()
        
        // 更新按钮状态
        updateFilterButtons(filter)
    }
    
    private fun updateFilterButtons(selectedFilter: OrderFilter) {
        applyFilterChipStyle(binding.allOrdersButton, selectedFilter == OrderFilter.ALL)
        applyFilterChipStyle(binding.pendingPaymentButton, selectedFilter == OrderFilter.PENDING_PAYMENT)
        applyFilterChipStyle(binding.pendingShipmentButton, selectedFilter == OrderFilter.PENDING_SHIPMENT)
        applyFilterChipStyle(binding.shippedButton, selectedFilter == OrderFilter.SHIPPED)
        applyFilterChipStyle(binding.deliveredButton, selectedFilter == OrderFilter.DELIVERED)
    }

    private fun applyFilterChipStyle(btn: MaterialButton, selected: Boolean) {
        val neon = ContextCompat.getColor(this, R.color.neon_teal)
        val secondary = ContextCompat.getColor(this, R.color.text_secondary)
        val w = (1.5f * resources.displayMetrics.density).toInt().coerceAtLeast(1)
        if (selected) {
            btn.strokeWidth = w
            btn.strokeColor = ColorStateList.valueOf(neon)
            btn.setTextColor(neon)
            btn.backgroundTintList = ColorStateList.valueOf(0x332EC4B6.toInt())
        } else {
            btn.strokeWidth = 0
            btn.strokeColor = ColorStateList.valueOf(Color.TRANSPARENT)
            btn.setTextColor(secondary)
            btn.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        }
    }
    
    private fun viewOrderDetail(order: Order) {
        // TODO: 实现订单详情页面
        UiFeedback.toast(this, "查看订单：${order.orderNumber}")
    }
    
    private fun payOrder(order: Order) {
        if (order.status == OrderStatus.PENDING_PAYMENT) {
            android.app.AlertDialog.Builder(this)
                .setTitle("确认支付")
                .setMessage("确定要支付订单 ${order.orderNumber} 吗？\n支付金额：¥${order.totalAmount}")
                .setPositiveButton("立即支付") { dialog, which ->
                    // 模拟支付成功
                    val index = orders.indexOfFirst { it.id == order.id }
                    if (index != -1) {
                        orders[index] = order.copy(status = OrderStatus.PENDING_SHIPMENT)
                        orderAdapter.notifyItemChanged(index)
                        updateOrderCount()
                        UiFeedback.toast(this, "支付成功，订单已进入待发货状态")
                    }
                }
                .setNegativeButton("取消", null)
                .show()
        } else {
            UiFeedback.toast(this, "该订单无需支付")
        }
    }
    
    private fun confirmReceipt(order: Order) {
        if (order.status == OrderStatus.SHIPPED) {
            android.app.AlertDialog.Builder(this)
                .setTitle("确认收货")
                .setMessage("确定要确认收货订单 ${order.orderNumber} 吗？")
                .setPositiveButton("确认收货") { dialog, which ->
                    // 模拟确认收货
                    val index = orders.indexOfFirst { it.id == order.id }
                    if (index != -1) {
                        orders[index] = order.copy(status = OrderStatus.DELIVERED)
                        orderAdapter.notifyItemChanged(index)
                        updateOrderCount()
                        UiFeedback.toast(this, "已确认收货，感谢您的支持！")
                    }
                }
                .setNegativeButton("取消", null)
                .show()
        } else {
            UiFeedback.toast(this, "该订单无法确认收货")
        }
    }
    
    private fun cancelOrder(order: Order) {
        if (order.status == OrderStatus.PENDING_PAYMENT || order.status == OrderStatus.PENDING_SHIPMENT) {
            android.app.AlertDialog.Builder(this)
                .setTitle("取消订单")
                .setMessage("确定要取消订单 ${order.orderNumber} 吗？")
                .setPositiveButton("确定取消") { dialog, which ->
                    // 模拟取消订单
                    val index = orders.indexOfFirst { it.id == order.id }
                    if (index != -1) {
                        orders.removeAt(index)
                        orderAdapter.notifyItemRemoved(index)
                        updateOrderCount()
                        updateEmptyState()
                        UiFeedback.toast(this, "订单已取消")
                    }
                }
                .setNegativeButton("不取消", null)
                .show()
        } else {
            UiFeedback.toast(this, "该订单无法取消")
        }
    }
    
    private fun reorder(order: Order) {
        if (order.status == OrderStatus.DELIVERED) {
            android.app.AlertDialog.Builder(this)
                .setTitle("再次购买")
                .setMessage("确定要再次购买订单 ${order.orderNumber} 中的商品吗？")
                .setPositiveButton("确定") { dialog, which ->
                    // 模拟加入购物车
                    UiFeedback.toast(this, "商品已加入购物车")
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)
                }
                .setNegativeButton("取消", null)
                .show()
        } else {
            UiFeedback.toast(this, "只有已完成的订单可以再次购买")
        }
    }
    
    private fun updateOrderCount() {
        val allCount = orders.size
        val pendingPaymentCount = orders.count { it.status == OrderStatus.PENDING_PAYMENT }
        val pendingShipmentCount = orders.count { it.status == OrderStatus.PENDING_SHIPMENT }
        val shippedCount = orders.count { it.status == OrderStatus.SHIPPED }
        val deliveredCount = orders.count { it.status == OrderStatus.DELIVERED }
        
        binding.allOrdersButton.text = "全部($allCount)"
        binding.pendingPaymentButton.text = "待支付($pendingPaymentCount)"
        binding.pendingShipmentButton.text = "待发货($pendingShipmentCount)"
        binding.shippedButton.text = "已发货($shippedCount)"
        binding.deliveredButton.text = "已完成($deliveredCount)"
    }
    
    private fun updateEmptyState() {
        if (orders.isEmpty()) {
            binding.emptyStateLayout.visibility = android.view.View.VISIBLE
            binding.orderRecyclerView.visibility = android.view.View.GONE
        } else {
            binding.emptyStateLayout.visibility = android.view.View.GONE
            binding.orderRecyclerView.visibility = android.view.View.VISIBLE
        }
    }
    
    @Suppress("DEPRECATION")
    private fun continueShopping() {
        val intent = Intent(this, ShopActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
    
    data class Order(
        val id: String,
        val orderNumber: String,
        var status: OrderStatus,
        val totalAmount: Double,
        val createTime: String,
        val items: List<OrderItem>,
        val shippingAddress: String,
        val receiverName: String,
        val receiverPhone: String
    )
    
    data class OrderItem(
        val product: Product,
        val quantity: Int,
        val price: Double
    )
    
    enum class OrderStatus {
        PENDING_PAYMENT,    // 待支付
        PENDING_SHIPMENT,    // 待发货
        SHIPPED,            // 已发货
        DELIVERED           // 已完成
    }
    
    enum class OrderFilter {
        ALL,                // 全部
        PENDING_PAYMENT,    // 待支付
        PENDING_SHIPMENT,   // 待发货
        SHIPPED,            // 已发货
        DELIVERED           // 已完成
    }
}
