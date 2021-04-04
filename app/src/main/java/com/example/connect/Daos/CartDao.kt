package com.example.connect.Daos

import com.example.connect.Models.Cart
import com.example.connect.Models.CartItem
import com.example.connect.Models.Shop
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

class CartDao {

    private val db = FirebaseFirestore.getInstance()
    val cartCollection = db.collection("Cart")
    private val auth = Firebase.auth
    fun createNewCart(uid:String){
        val newCart:ArrayList<CartItem> = ArrayList()
        cartCollection.document(uid).set(Cart(uid,newCart))
    }
    fun addCart(uid:String,cartItem: CartItem) {
        GlobalScope.launch {
            val cart = getCartById(uid).await().toObject(Cart::class.java)
            if (cart != null) {
                cart.cartList.add(cartItem)
                cartCollection.document(uid).set(cart)
            } else {
                val newCart: ArrayList<CartItem> = ArrayList()
                newCart.add(cartItem)
                cartCollection.document(uid).set(Cart(uid, newCart))
            }
        }
    }
    fun removeItem(uid:String,cartItem: CartItem){
        GlobalScope.launch {
            val cart = getCartById(uid).await().toObject(Cart::class.java)!!
            cart.cartList.remove(cartItem)
            cartCollection.document(uid).set(cart)
        }
    }
    fun getCartById(uId: String): Task<DocumentSnapshot> {
        return cartCollection.document(uId).get()
    }

}