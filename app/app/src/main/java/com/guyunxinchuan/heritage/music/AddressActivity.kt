package com.guyunxinchuan.heritage.music

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.guyunxinchuan.heritage.music.databinding.ActivityAddressBinding

class AddressActivity : BaseActivity() {
    
    private lateinit var binding: ActivityAddressBinding
    private lateinit var addressAdapter: AddressAdapter
    
    private val addressList = mutableListOf(
        Address("1", "张三", "13800138000", "北京市朝阳区建国路88号", true),
        Address("2", "李四", "13900139000", "上海市浦东新区陆家嘴环路1000号", false),
        Address("3", "王五", "13700137000", "广州市天河区珠江新城", false)
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyToActivity(
            this,
            binding.addressRoot,
            binding.addressTopInsetHost,
            bottomBar = null,
            scrollOrListBottomInset = binding.addressRecyclerView,
            scrollBottomPaddingDp = 16f,
        )

        setupViews()
        setupRecyclerView()
    }
    
    private fun setupViews() {
        binding.backButton.setOnClickListener {
            finish()
        }
        
        binding.addButton.setOnClickListener {
            showAddAddressDialog()
        }
    }
    
    private fun setupRecyclerView() {
        addressAdapter = AddressAdapter(addressList, 
            onEdit = { address ->
                showEditAddressDialog(address)
            },
            onDelete = { address ->
                showDeleteConfirmDialog(address)
            },
            onSetDefault = { address ->
                setDefaultAddress(address)
            }
        )
        
        binding.addressRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AddressActivity)
            adapter = addressAdapter
        }
    }
    
    private fun showAddAddressDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("添加收货地址")
        
        val input = android.widget.EditText(this)
        input.hint = "请输入收货地址"
        builder.setView(input)
        
        builder.setPositiveButton("确定") { _, _ ->
            val address = input.text.toString().trim()
            if (address.isNotEmpty()) {
                val newAddress = Address(
                    id = System.currentTimeMillis().toString(),
                    name = "用户",
                    phone = "138****0000",
                    detail = address,
                    isDefault = addressList.isEmpty()
                )
                addressList.add(0, newAddress)
                addressAdapter.notifyItemInserted(0)
                UiFeedback.toast(this, "地址添加成功")
            }
        }
        builder.setNegativeButton("取消", null)
        builder.show()
    }
    
    private fun showEditAddressDialog(address: Address) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("编辑收货地址")
        
        val input = android.widget.EditText(this)
        input.setText(address.detail)
        builder.setView(input)
        
        builder.setPositiveButton("确定") { _, _ ->
            val newDetail = input.text.toString().trim()
            if (newDetail.isNotEmpty()) {
                val index = addressList.indexOf(address)
                if (index != -1) {
                    addressList[index] = address.copy(detail = newDetail)
                    addressAdapter.notifyItemChanged(index)
                    UiFeedback.toast(this, "地址修改成功")
                }
            }
        }
        builder.setNegativeButton("取消", null)
        builder.show()
    }
    
    private fun showDeleteConfirmDialog(address: Address) {
        AlertDialog.Builder(this)
            .setTitle("删除地址")
            .setMessage("确定要删除这个地址吗？")
            .setPositiveButton("确定") { _, _ ->
                val index = addressList.indexOf(address)
                if (index != -1) {
                    addressList.removeAt(index)
                    addressAdapter.notifyItemRemoved(index)
                    UiFeedback.toast(this, "地址已删除")
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun setDefaultAddress(address: Address) {
        val index = addressList.indexOf(address)
        if (index != -1) {
            addressList.forEachIndexed { i, addr ->
                addressList[i] = addr.copy(isDefault = i == index)
            }
            addressAdapter.notifyDataSetChanged()
            UiFeedback.toast(this, "已设为默认地址")
        }
    }
    
    data class Address(
        val id: String,
        val name: String,
        val phone: String,
        val detail: String,
        val isDefault: Boolean
    )
}
