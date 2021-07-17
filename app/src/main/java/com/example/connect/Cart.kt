package com.example.connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.connect.Daos.CartDao
import com.example.connect.Daos.ShopDao
import com.example.connect.Models.Cart
import com.example.connect.Models.CartItem
import com.example.connect.Models.Order
import com.example.connect.Models.Shop
import com.example.connect.Models.item
import com.example.connect.Notification.MySingletonClass
import com.example.socialapp.daos.UserDao
import com.example.socialapp.models.User
import com.google.firebase.database.*
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_shop_items.*
import kotlinx.android.synthetic.main.activity_shop_items.recyclerView
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.custom_dialog.addreesOfOreder
import kotlinx.android.synthetic.main.custom_dialog.cancel_button
import kotlinx.android.synthetic.main.custom_dialog.placeOrder
import kotlinx.android.synthetic.main.custom_dialog.quantity
import kotlinx.android.synthetic.main.custom_dialog_2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

class Cart : AppCompatActivity(),IcartItemClicked {
    lateinit var adapter: cartItemAdapter
    lateinit var uid: String
    lateinit var shopDao: ShopDao
    lateinit var cartDao: CartDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        uid = intent.getStringExtra("uid").toString()
        shopDao = ShopDao()
         cartDao = CartDao()
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = cartItemAdapter(this)
        val docRef = cartDao.cartCollection.document(uid)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            print("data is changed and listneed ")
            if (snapshot != null&&snapshot.exists()) {
                GlobalScope.launch {
                    val cart: Cart? = cartDao.getCartById(uid).await().toObject(Cart::class.java)
                    if (cart == null) {
                        Log.d("cart", "insidethis null")
                        cartDao.createNewCart(uid)
                    } else
                        withContext(Dispatchers.Main) {
                            updateList(cart.cartList)
                        }
                }
            }
        }
        recyclerView.adapter = adapter
    }

    fun updateList(newList: ArrayList<CartItem>) {
        adapter.updateList(newList)
    }

    override fun OnitemClicked(cartItem: CartItem) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_2, null)
        val builder = AlertDialog.Builder(this).setView(mDialogView).setTitle("Place your Order")
        val mAlertDialog = builder.show()
        mAlertDialog.placeOrder.setOnClickListener {
            val nameOfOrder: String = cartItem.item.name
            val quantity: String = mAlertDialog.quantity.text.toString()
            val addressOfOreder: String = mAlertDialog.addreesOfOreder.text.toString()
            if (addressOfOreder.isEmpty() || quantity.isEmpty()) {
                Toast.makeText(this, "some of the entities are empty", Toast.LENGTH_SHORT).show()
            } else {
                GlobalScope.launch {
                    val userDao = UserDao()
                    val user = userDao.getUserById(uid).await().toObject(User::class.java)!!
                    val order = Order(user, nameOfOrder, quantity, addressOfOreder)
                    shopDao.placeOrder(cartItem.orderTo, order)
                }
                Toast.makeText(this, "Ordered successfully ", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
                GlobalScope.launch {
                    val shopDao:ShopDao = ShopDao()
                    val shop = shopDao.getShopById(cartItem.orderTo).await().toObject(Shop::class.java)
                    if (shop != null) {
                        sendNotification("ORDER : ","Hey you get a order for $nameOfOrder",shop.userId)
                    }
                }
            }
        }
        mAlertDialog.remove.setOnClickListener {
            cartDao.removeItem(uid, cartItem)
            Toast.makeText(this, "removed successfully ", Toast.LENGTH_SHORT).show()
            mAlertDialog.dismiss()
        }
        mAlertDialog.cancel_button.setOnClickListener {
            Log.d("checking","here i can come5")
            mAlertDialog.dismiss()
        }
    }
    private fun sendNotification(
        heading: String,
        massage: String,
        userId: String
    ){
        val json = JSONObject()
        try {
            json.put("to", "/topics/$userId")
            val notificationObj = JSONObject()
            notificationObj.put("title", heading)
            notificationObj.put("body", massage)
            json.put("notification", notificationObj)
            val url = "https://fcm.googleapis.com/fcm/send"
            val accessTokenRequest: JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST, url, json,
                Response.Listener<JSONObject?> { response ->
                    Log.d("noty", "in sccesfull send the noty")
                }, Response.ErrorListener {
                    Log.d("noty", "error $it")
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String?, String?> {
                    val header: HashMap<String?, String> = HashMap()
                    header["content-type"] = "application/json"
                    header["authorization"] =
                        "key=AAAAlk9ER2I:APA91bHm4C7bMWY9eiXS6xCUFCSwNPwSaB9BBeuIwn0PoWoNuCTzZTB5jABAK9P15fn8B4zgHruDiIr0kkGpyxGy2IA8iO7_-w4uWj7h4JmtEudTa3q95OEsEVAnSGpBr2ae3PE9cvqZ"
                    return header
                }
            }
            MySingletonClass.getInstance(this).addToRequestQueue(accessTokenRequest)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
//        {
//            "To": "topics/topic name",
//            notification : {
//            title : "",
//            body : ""
//        },
//          data : {
//      brandId : "puma",
//      cat : "shoes"
//     }
//        }
    }
}