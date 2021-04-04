package com.example.connect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.connect.Models.item
import org.w3c.dom.Text

class shopItemAdapter(val listner: IshopItemClicked,val isBuyer:Boolean) : RecyclerView.Adapter<shopItemAdapter.shopItemViewHolder>() {
   private val itemList=ArrayList<item>()
    class shopItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       val nameOfItem:TextView = itemView.findViewById(R.id.nameOfItem)
        val priceOfItem:TextView =itemView.findViewById(R.id.priceOfItem)
        val aboutItem:TextView = itemView.findViewById(R.id.aboutItem)
        val cartButton:ImageView = itemView.findViewById(R.id.cart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): shopItemViewHolder {
      val view:View = LayoutInflater.from(parent.context).inflate(R.layout.item_item_shop, parent, false)
        val viewHolder = shopItemViewHolder(view)

      view.setOnClickListener {
          listner.OnitemClicked(itemList[viewHolder.adapterPosition])

      }
        if(isBuyer)
            viewHolder.cartButton.visibility = View.VISIBLE
        else viewHolder.cartButton.visibility = View.GONE
        viewHolder.cartButton.setOnClickListener {
            listner.OnCartCliked(itemList[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: shopItemViewHolder, position: Int) {
         holder.nameOfItem.text= itemList[position].name
        holder.priceOfItem.text = itemList[position].price
        holder.aboutItem.text = itemList[position].about
    }
    fun updateList(newList:ArrayList<item>){
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }
}
interface IshopItemClicked{
    fun OnitemClicked(item: item)
    fun OnCartCliked(item: item)
}