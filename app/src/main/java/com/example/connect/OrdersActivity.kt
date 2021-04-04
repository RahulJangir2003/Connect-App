package com.example.connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.connect.Daos.ShopDao
import com.example.connect.Models.Order
import com.example.connect.Models.Shop
import kotlinx.android.synthetic.main.activity_orders.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class OrdersActivity : AppCompatActivity(), IorderClicked {
    private lateinit var shopDao: ShopDao
    lateinit var adapter: OrederAdapter
    lateinit var shopId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        shopId = intent.getStringExtra("shopId").toString()
        shopDao = ShopDao()
        recyclerView.layoutManager = LinearLayoutManager(this)
         adapter = OrederAdapter(this)
        val docRef = shopDao.shopCollections.document(shopId)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            print("data is changed and listneed ")
            if (snapshot != null&&snapshot.exists()) {
                GlobalScope.launch {
                    val shop: Shop = shopDao.getShopById(shopId).await().toObject(Shop::class.java)!!
                    withContext(Dispatchers.Main){
                        adapter.updateList(shop.orders)
                    }
                }
            }
        }
        recyclerView.adapter = adapter
    }

    override fun OnOrderClicked(order: Order) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("have you sent this order ??")
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            shopDao.orderDone(shopId,order)
            Toast.makeText(this,"Order sent successfully ",Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Not Yet"){dialogInterface, which ->
            builder.show().dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}