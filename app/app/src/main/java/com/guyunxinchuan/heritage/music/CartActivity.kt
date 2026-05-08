package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.guyunxinchuan.heritage.music.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter
    
    // 模拟购物车数据
    private val cartItems = mutableListOf(
        CartItem(
            id = "1",
            product = Product(
                id = "1",
                name = "古琴 - 仲尼式",
                price = 2999.0,
                rating = 4.8f,
                description = "传统仲尼式古琴，手工制作",
                imageUrl = "",
                category = "乐器",
                tags = listOf("古琴", "传统", "手工"),
                imageResId = R.drawable.banner2_img,
            ),
            quantity = 1,
            selected = true
        ),
        CartItem(
            id = "2",
            product = Product(
                id = "2",
                name = "二胡 - 专业级",
                price = 899.0,
                rating = 4.7f,
                description = "专业演奏级二胡，音色优美",
                imageUrl = "",
                category = "乐器",
                tags = listOf("二胡", "专业", "演奏级"),
                imageResId = R.drawable.banner3_img,
            ),
            quantity = 1,
            selected = true
        ),
        CartItem(
            id = "3",
            product = Product(
                id = "3",
                name = "琵琶教材 - 入门到精通",
                price = 99.0,
                rating = 4.9f,
                description = "琵琶演奏教材，适合初学者",
                imageUrl = "",
                category = "教材",
                tags = listOf("琵琶", "教材", "教学"),
                imageResId = R.drawable.banner1_img,
            ),
            quantity = 2,
            selected = false
        )
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyToActivity(
            this,
            binding.cartRoot,
            binding.cartTopInsetHost,
            binding.bottomBar,
            scrollOrListBottomInset = null,
        )

        binding.selectAllCheckBox.buttonTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.neon_teal))

        setupViews()
        setupRecyclerView()
        updateTotal()
        updateSelectAllState()
    }
    
    private fun setupViews() {
        // 隐藏ActionBar(使用自定义顶部栏)
        supportActionBar?.hide()
        
        // 设置返回按钮
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        
        // 设置编辑按钮
        binding.editButton.setOnClickListener {
            toggleEditMode()
        }
        
        // 全选监听在 updateSelectAllState 中绑定，避免程序化 setChecked 触发误操作
        
        // 设置结算按钮
        binding.checkoutButton.setOnClickListener {
            checkout()
        }
        
        // 设置清空购物车按钮
        binding.clearCartButton.setOnClickListener {
            clearCart()
        }
        
        // 设置继续购物按钮
        binding.continueShoppingButton.setOnClickListener {
            continueShopping()
        }

        binding.goAddressButton.setOnClickListener {
            startActivity(Intent(this, AddressActivity::class.java))
        }

        // 更新空状态
        updateEmptyState()
    }
    
    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(cartItems) { item, action ->
            when (action) {
                CartAdapter.Action.QUANTITY_CHANGE -> updateItemQuantity(item)
                CartAdapter.Action.SELECTION_CHANGE -> updateItemSelection(item)
                CartAdapter.Action.DELETE -> deleteItem(item)
            }
        }
        
        binding.cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
        }
    }
    
    private fun updateItemQuantity(item: CartItem) {
        // 更新商品数量
        val index = cartItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            cartItems[index] = item
            updateTotal()
        }
    }
    
    private fun updateItemSelection(item: CartItem) {
        // 更新商品选择状态
        val index = cartItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            cartItems[index] = item
            updateTotal()
            updateSelectAllState()
        }
    }
    
    private fun deleteItem(item: CartItem) {
        // 删除商品
        cartItems.removeAll { it.id == item.id }
        cartAdapter.notifyDataSetChanged()
        updateTotal()
        updateEmptyState()
        updateSelectAllState()
        
        UiFeedback.toast(this, "已删除商品")
    }
    
    private fun toggleEditMode() {
        val isEditMode = binding.editButton.tag as? Boolean ?: false
        val newEditMode = !isEditMode
        
        binding.editButton.tag = newEditMode
        if (newEditMode) {
            binding.editButton.text = "完成"
            binding.clearCartButton.visibility = android.view.View.VISIBLE
            cartAdapter.setEditMode(true)
        } else {
            binding.editButton.text = "编辑"
            binding.clearCartButton.visibility = android.view.View.GONE
            cartAdapter.setEditMode(false)
        }
    }
    
    private fun selectAllItems(selectAll: Boolean) {
        cartItems.forEach { it.selected = selectAll }
        cartAdapter.notifyDataSetChanged()
        updateTotal()
        updateSelectAllState()
    }
    
    private fun updateSelectAllState() {
        val allSelected = cartItems.isNotEmpty() && cartItems.all { it.selected }
        binding.selectAllCheckBox.setOnCheckedChangeListener(null)
        binding.selectAllCheckBox.isChecked = allSelected
        binding.selectAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            selectAllItems(isChecked)
        }
    }
    
    private fun updateTotal() {
        val selectedItems = cartItems.filter { it.selected }
        val totalQuantity = selectedItems.sumOf { it.quantity }
        val totalPrice = selectedItems.sumOf { it.product.price * it.quantity }
        
        binding.totalQuantityTextView.text = "共${totalQuantity}件"
        binding.totalPriceTextView.text = "¥${String.format("%.2f", totalPrice)}"
        
        // 更新结算按钮状态
        binding.checkoutButton.isEnabled = selectedItems.isNotEmpty()
        binding.checkoutButton.text = if (selectedItems.isNotEmpty()) {
            "结算(${selectedItems.size})"
        } else {
            "结算"
        }
    }
    
    private fun updateEmptyState() {
        if (cartItems.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.cartContentLayout.visibility = View.GONE
            binding.bottomBar.visibility = View.GONE
            binding.promoBannerCard.visibility = View.GONE
            binding.goAddressButton.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.cartContentLayout.visibility = View.VISIBLE
            binding.bottomBar.visibility = View.VISIBLE
            binding.promoBannerCard.visibility = View.VISIBLE
            binding.goAddressButton.visibility = View.VISIBLE
        }
    }
    
    private fun checkout() {
        val selectedItems = cartItems.filter { it.selected }
        if (selectedItems.isEmpty()) {
            UiFeedback.toast(this, "请选择要结算的商品")
            return
        }
        
        val totalPrice = selectedItems.sumOf { it.product.price * it.quantity }
        
        val intent = Intent(this, OrderActivity::class.java)
        intent.putExtra("cart_items", ArrayList(selectedItems))
        intent.putExtra("total_price", totalPrice)
        startActivity(intent)
    }
    
    private fun clearCart() {
        if (cartItems.isEmpty()) {
            UiFeedback.toast(this, "购物车已经是空的")
            return
        }
        
        android.app.AlertDialog.Builder(this)
            .setTitle("清空购物车")
            .setMessage("确定要清空购物车中的所有商品吗？")
            .setPositiveButton("确定") { dialog, which ->
                cartItems.clear()
                cartAdapter.notifyDataSetChanged()
                updateTotal()
                updateEmptyState()
                updateSelectAllState()
                UiFeedback.toast(this, "购物车已清空")
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    @Suppress("DEPRECATION")
    private fun continueShopping() {
        val intent = Intent(this, ShopActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
    
    data class CartItem(
        val id: String,
        val product: Product,
        var quantity: Int,
        var selected: Boolean
    )
}
