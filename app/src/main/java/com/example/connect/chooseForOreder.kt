package com.example.connect

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.connect.Daos.ShopDao
import com.example.connect.Models.Shop
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.activity_shop.*

class chooseForOreder: AppCompatActivity(), IshopAdapter {
    private lateinit var shopDao: ShopDao
    private lateinit var adapter: ShopAdapter
    lateinit var uid: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
//        deleteShop.visibility = View.VISIBLE
        fab.visibility = View.GONE
        uid = intent.getStringExtra("uid").toString()
        setUpRecyclerView();
    }

    private fun setUpRecyclerView() {
        shopDao = ShopDao()
        val shopCollections = shopDao.shopCollections
        val query = shopCollections.whereEqualTo("userId", uid)
        val recyclerViewOptions =
            FirestoreRecyclerOptions.Builder<Shop>().setQuery(query, Shop::class.java).build()

        adapter = ShopAdapter(recyclerViewOptions, this, false)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onShopClicked(shopId: String) {
        updateUI(shopId)
    }

    override fun onDeleteClicked(shopId: String) {
    }

    fun updateUI(shopId: String) {
        val intent = Intent(this, OrdersActivity::class.java)
        intent.putExtra("shopId", shopId)
        startActivity(intent)
    }

}