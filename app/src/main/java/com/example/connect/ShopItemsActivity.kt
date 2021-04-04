package com.example.connect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.connect.Daos.ShopDao
import com.example.connect.Models.Shop
import com.example.connect.Models.item
import com.google.firebase.database.FirebaseDatabase
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.activity_shop_items.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ShopItemsActivity : AppCompatActivity(), IshopItemClicked {
    private lateinit var shopDao: ShopDao
    lateinit var adapter: shopItemAdapter
    lateinit var shopId:String
    val TAG:String = "change litner"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_items)
         shopId = intent.getStringExtra("shopId").toString()
        OneSignal.startInit(this).init()
        OneSignal.setSubscription(true)
        OneSignal.idsAvailable(OneSignal.IdsAvailableHandler { userId, registrationId ->
            FirebaseDatabase.getInstance().reference.child(shopId).child("notificationKey").setValue(userId);
        })
        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)

        fab.setOnClickListener {
            val addItemIntent = Intent(this,AddItemActivity::class.java)
            addItemIntent.putExtra("shopId",shopId)
            startActivity(addItemIntent)

        }
        shopDao = ShopDao()

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = shopItemAdapter(this,false)
       val docRef = shopDao.shopCollections.document(shopId)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            print("data is changed and listneed ")
            if (snapshot != null&&snapshot.exists()) {
                GlobalScope.launch {
                    val shop:Shop = shopDao.getShopById(shopId).await().toObject(Shop::class.java)!!
                    withContext(Dispatchers.Main){
                        updateList(shop.items)
                    }
                }
            }
        }

        recyclerView.adapter = adapter
    }
    fun updateList(newList:ArrayList<item>){
        adapter.updateList(newList)
    }

    override fun OnitemClicked(item: item) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Are you sure to delete ${item.name}")
        alertDialogBuilder.setPositiveButton("Yes"){dialogInterface, which ->
            shopDao.deleteShopItem(shopId,item)
            Toast.makeText(this,"deleted successfully ",Toast.LENGTH_SHORT).show()
        }
        alertDialogBuilder.setNegativeButton("No"){dialogInterface, which ->
        }
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun OnCartCliked(item: item) {
        TODO("Not yet implemented")
    }

}
