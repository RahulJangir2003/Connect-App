package com.example.connect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.connect.Daos.ShopDao
import com.example.connect.Models.Order
import com.example.connect.Models.Shop
import com.example.socialapp.daos.UserDao
import com.example.socialapp.models.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_buyer.*
import kotlinx.android.synthetic.main.item_shop.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BuyerActivity : AppCompatActivity(), IshopAdapter {
    private lateinit var shopDao: ShopDao
    private lateinit var adapter: ShopAdapter
    lateinit var uid:String
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buyer)
        setUpRecyclerView()
        uid = intent.getStringExtra("uid").toString()
        drawerLayout = findViewById(R.id.drawerLayout)
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        actionBarToggle.syncState()
        navView = findViewById(R.id.navView)
        GlobalScope.launch(Dispatchers.IO) {
            val userDao = UserDao()
            val user = userDao.getUserById(uid).await().toObject(User::class.java)!!
            withContext(Dispatchers.Main) {
                Glide.with(this@BuyerActivity).load(user.imageUrl).circleCrop().into(navView.ProfileImage)
            }
        }
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.myProfile -> {
                    Toast.makeText(this, "Yet to be implemented!!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.shops -> {
                    val intent = Intent(this,shopActivity::class.java)
                    intent.putExtra("uid",uid)
                    startActivity(intent)
                    true
                }
                R.id.orders -> {
                    val intent = Intent(this,chooseForOreder::class.java)
                    intent.putExtra("uid",uid)
                    startActivity(intent)
                    true
                }
                R.id.setting -> {
                    Toast.makeText(this, "Yet to be implemented!!", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }
    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
    private fun setUpRecyclerView() {
        shopDao = ShopDao()
        val shopCollections = shopDao.shopCollections
        val query = shopCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Shop>().setQuery(query, Shop::class.java).build()

        adapter = ShopAdapter(recyclerViewOptions,this,false)

        recyclerViewBuyer.adapter = adapter
        recyclerViewBuyer.layoutManager = LinearLayoutManager(this)
    }

    override fun onShopClicked(shopId: String) {
        val intent = Intent(this,BuyerShopItemActivity::class.java)
        intent.putExtra("shopId",shopId)
        intent.putExtra("uid",uid)
        startActivity(intent)
    }

    override fun onDeleteClicked(shopId: String) {

    }
    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(navView)
        return true
    }

    // override the onBackPressed() function to close the Drawer when the back button is clicked
    override fun onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true;
    }
        override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.Cart){
            val intent = Intent(this,Cart::class.java)
            intent.putExtra("uid",uid)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}