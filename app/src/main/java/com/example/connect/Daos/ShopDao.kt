package com.example.connect.Daos

import com.example.connect.Models.Order
import com.example.connect.Models.Shop
import com.example.connect.Models.item
import com.example.socialapp.daos.UserDao
import com.example.socialapp.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ShopDao {
    private val db = FirebaseFirestore.getInstance()
    val shopCollections = db.collection("shops")
    private val auth = Firebase.auth
    fun addShop(uid:String,type:String,name:String,address: String) {
        GlobalScope.launch {
            val currentUserId = auth.currentUser!!.uid
            val userDao = UserDao()
            val user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
            val currentTime = System.currentTimeMillis()
            val shop = Shop(uid,type,name,user,currentTime,address)
            shopCollections.document().set(shop)
        }
    }
    fun getShopById(shopId: String): Task<DocumentSnapshot> {
        return shopCollections.document(shopId).get()
    }
   fun updateShopItem(shopId: String,item: item){
        GlobalScope.launch {
            val shop =getShopById(shopId).await().toObject(Shop::class.java)!!
            shop.items.add(item)
            shopCollections.document(shopId).set(shop)
        }
   }
    fun placeOrder(shopId: String,order: Order){
        GlobalScope.launch {
            val shop =getShopById(shopId).await().toObject(Shop::class.java)!!
            shop.orders.add(order)
            shopCollections.document(shopId).set(shop)
        }
    }
    fun orderDone(shopId: String,order: Order){
        GlobalScope.launch {
            val shop =getShopById(shopId).await().toObject(Shop::class.java)!!
            shop.orders.remove(order)
            shopCollections.document(shopId).set(shop)
        }
    }
    fun deleteShop(shopId: String){
        GlobalScope.launch {
            shopCollections.document(shopId).delete()
        }
    }
    fun deleteShopItem(shopId: String,item: item){
        GlobalScope.launch {
            val shop =getShopById(shopId).await().toObject(Shop::class.java)!!
            shop.items.remove(item)
            shopCollections.document(shopId).set(shop)
        }
    }
}