package com.example.connect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.connect.Daos.ShopDao
import com.example.connect.Models.Shop
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_shop.*
import kotlinx.android.synthetic.main.item_shop.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class shopActivity : AppCompatActivity(), IshopAdapter {
    private lateinit var shopDao: ShopDao
    private lateinit var adapter: ShopAdapter
    lateinit var uid: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
//        deleteShop.visibility = View.VISIBLE
        uid = intent.getStringExtra("uid").toString()
        fab.setOnClickListener {
            val intent = Intent(this, CreateShop::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
        }
        setUpRecyclerView();
    }

    private fun setUpRecyclerView() {
        shopDao = ShopDao()
        val shopCollections = shopDao.shopCollections
        val query = shopCollections.whereEqualTo("userId", uid)
        val recyclerViewOptions =
            FirestoreRecyclerOptions.Builder<Shop>().setQuery(query, Shop::class.java).build()

        adapter = ShopAdapter(recyclerViewOptions, this, true)

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
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Are you sure to delete this Shop")
        alertDialogBuilder.setPositiveButton("Yes") { dialogInterface, which ->
            shopDao.deleteShop(shopId)
            Toast.makeText(this, "deleted successfully ", Toast.LENGTH_SHORT).show()
        }
        alertDialogBuilder.setNegativeButton("No") { dialogInterface, which ->
            alertDialogBuilder.show().dismiss()
        }
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun updateUI(shopId: String) {
        val intent = Intent(this, ShopItemsActivity::class.java)
        intent.putExtra("shopId", shopId)
//        intent.putExtra("shop",shop)
        startActivity(intent)
    }

}