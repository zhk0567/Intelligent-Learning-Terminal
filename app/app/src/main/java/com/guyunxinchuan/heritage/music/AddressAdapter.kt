package com.guyunxinchuan.heritage.music

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AddressAdapter(
    private val addressList: List<AddressActivity.Address>,
    private val onEdit: (AddressActivity.Address) -> Unit,
    private val onDelete: (AddressActivity.Address) -> Unit,
    private val onSetDefault: (AddressActivity.Address) -> Unit
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    class AddressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.addressName)
        val phoneText: TextView = view.findViewById(R.id.addressPhone)
        val detailText: TextView = view.findViewById(R.id.addressDetail)
        val defaultBadge: TextView = view.findViewById(R.id.defaultBadge)
        val editButton: ImageView = view.findViewById(R.id.editButton)
        val deleteButton: ImageView = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_address, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addressList[position]
        
        holder.nameText.text = address.name
        holder.phoneText.text = address.phone
        holder.detailText.text = address.detail
        
        if (address.isDefault) {
            holder.defaultBadge.visibility = View.VISIBLE
        } else {
            holder.defaultBadge.visibility = View.GONE
        }
        
        holder.editButton.setOnClickListener {
            onEdit(address)
        }
        
        holder.deleteButton.setOnClickListener {
            onDelete(address)
        }
        
        holder.itemView.setOnClickListener {
            if (!address.isDefault) {
                onSetDefault(address)
            }
        }
    }

    override fun getItemCount() = addressList.size
}
