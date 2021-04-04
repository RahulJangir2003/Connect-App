package com.example.connect

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.connect.Models.CartItem
import com.example.connect.Models.item

class cartItemAdapter(val listner: IcartItemClicked) : RecyclerView.Adapter<cartItemAdapter.cartItemViewHolder>() {
    private val itemList=ArrayList<CartItem>()
    class cartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameOfItem: TextView = itemView.findViewById(R.id.nameOfItem)
        val priceOfItem: TextView =itemView.findViewById(R.id.priceOfItem)
        val aboutItem: TextView = itemView.findViewById(R.id.aboutItem)
        val cartButton:ImageView = itemView.findViewById(R.id.cart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cartItemViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_item_shop, parent, false)
        val viewHolder = cartItemViewHolder(view)
        viewHolder.cartButton.visibility = View.GONE
        view.setOnClickListener {
            listner.OnitemClicked(itemList[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: cartItemViewHolder, position: Int) {
        holder.nameOfItem.text= itemList[position].item.name
        holder.priceOfItem.text = itemList[position].item.price
        holder.aboutItem.text = itemList[position].item.about
    }
    fun updateList(newList:ArrayList<CartItem>){
        itemList.clear()
        itemList.addAll(newList)
        Log.d("cartcheck","insidethis new list came")
        notifyDataSetChanged()
    }
}
interface IcartItemClicked{
    fun OnitemClicked(cartItem: CartItem)
}