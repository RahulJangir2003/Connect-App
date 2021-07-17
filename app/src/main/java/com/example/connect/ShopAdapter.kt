package com.example.connect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connect.Daos.ShopDao
import com.example.connect.Models.Shop
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ShopAdapter(
    options: FirestoreRecyclerOptions<Shop>,
    val listner: IshopAdapter,val canDelete: Boolean
) : FirestoreRecyclerAdapter<Shop, ShopAdapter.ShopViewHolder>(
    options

) {

    class ShopViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val shopName: TextView = itemView.findViewById(R.id.name)
        val shopType: TextView = itemView.findViewById(R.id.type)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val shopAdd: TextView = itemView.findViewById(R.id.address)
        val deleteButton:ImageView = itemView.findViewById(R.id.deleteShop)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shop,parent,false)
        val viewHolder = ShopViewHolder(view)
        view.setOnClickListener {
            listner.onShopClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        if(canDelete)
        viewHolder.deleteButton.visibility = View.VISIBLE
        else
            viewHolder.deleteButton.visibility = View.GONE
        viewHolder.deleteButton.setOnClickListener {
            listner.onDeleteClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int, model: Shop) {
        holder.shopName.text = model.nameOfShop
        holder.shopType.text =model.typeOfShop
        holder.shopAdd.text = model.address
        Glide.with(holder.userImage.context).load(model.createdBy.imageUrl).circleCrop().into(holder.userImage)
    }
}
interface IshopAdapter {
    fun onShopClicked(shopId: String)
    fun onDeleteClicked(shopId: String)
}