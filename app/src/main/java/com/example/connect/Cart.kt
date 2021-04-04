package com.example.connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.connect.Daos.CartDao
import com.example.connect.Daos.ShopDao
import com.example.connect.Models.Cart
import com.example.connect.Models.CartItem
import com.example.connect.Models.Order
import com.example.connect.Models.Shop
import com.example.connect.Models.item
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
                val ref: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child(cartItem.orderTo)
                        .child("notificationKey");
                ref.addValueEventListener(
                    object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            Log.d("DataBaseError", error.toString())
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            sendNotification(
                                "ORDER : ",
                                "Hey you get a order for $nameOfOrder",
                                snapshot.value.toString()
                            )
                        }

                    }
                )
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
    private fun sendNotification(massage:String, heading:String ,notificationKey:String ){
        Log.d("notificationKeyShow",notificationKey)
        try {
            val notificationContent = JSONObject(
                "{'contents':{'en':'" + massage + "'},"+
                        "'include_player_ids':['" + notificationKey + "']," +
                        "'headings':{'en': '" + heading + "'}}")
            Log.d("jsonError","no error")
            OneSignal.postNotification(notificationContent,null)

        }catch (e: JSONException){
            Log.d("jsonError","$e")
            print("error ocuurrerd $e")
        }
    }
}