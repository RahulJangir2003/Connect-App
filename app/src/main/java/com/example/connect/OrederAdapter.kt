package com.example.connect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connect.Models.Order

class OrederAdapter(val listner:IorderClicked):RecyclerView.Adapter<OrederAdapter.orederItemViewHolder>() {
    private val itemList=ArrayList<Order>()
    class orederItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameOfOrder: TextView = itemView.findViewById(R.id.nameOfOrder)
        val quantity: TextView =itemView.findViewById(R.id.quantity)
        val address:TextView = itemView.findViewById(R.id.address)
        val userImage:ImageView = itemView.findViewById(R.id.userImage)
        val name:TextView = itemView.findViewById(R.id.name)
    }


    fun updateList(newList:ArrayList<Order>){
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): orederItemViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.order_item,parent,false)
        val viewHolder = orederItemViewHolder(view)
        view.setOnClickListener {
            listner.OnOrderClicked(itemList[viewHolder.adapterPosition])
        }
        return  viewHolder
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: orederItemViewHolder, position: Int) {
       holder.nameOfOrder.text = itemList[position].name
        holder.quantity.text = itemList[position].quantity
        holder.address.text = itemList[position].address
        Glide.with(holder.userImage.context).load(itemList[position].orderBy.imageUrl).circleCrop().into(holder.userImage)
        holder.name.text = itemList[position].orderBy.displayName
    }
}
interface IorderClicked{
    fun OnOrderClicked(order: Order)
}