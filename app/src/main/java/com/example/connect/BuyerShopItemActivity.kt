package com.example.connect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.connect.Daos.CartDao
import com.example.connect.Daos.ShopDao
import com.example.connect.Models.CartItem
import com.example.connect.Models.Order
import com.example.connect.Models.Shop
import com.example.connect.Models.item
import com.example.socialapp.daos.UserDao
import com.example.socialapp.models.User
import com.google.firebase.database.*
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.activity_buyer_shop_item.*
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.json.JSONException
import org.json.JSONObject

class BuyerShopItemActivity : AppCompatActivity(), IshopItemClicked {
    private lateinit var shopDao: ShopDao
    lateinit var adapter: shopItemAdapter
    lateinit var shopId:String
    lateinit var uid:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buyer_shop_item)
        Toast.makeText(this,"click on any item to place order",Toast.LENGTH_SHORT).show()
         shopId = intent.getStringExtra("shopId").toString()
        uid = intent.getStringExtra("uid").toString()
        shopDao = ShopDao()
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = shopItemAdapter(this,true)
        GlobalScope.launch {
            val shop: Shop = shopDao.getShopById(shopId).await().toObject(Shop::class.java)!!
            withContext(Dispatchers.Main){
                updateList(shop.items)
            }
        }
        recyclerView.adapter = adapter
    }
    fun updateList(newList:ArrayList<item>){
        adapter.updateList(newList)
    }

    override fun OnitemClicked(item: item) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val builder = AlertDialog.Builder(this).setView(mDialogView).setTitle("Place your Order")
        val  mAlertDialog = builder.show()
        mAlertDialog.placeOrder.setOnClickListener {
            val nameOfOrder:String = item.name
            val quantity:String = mAlertDialog.quantity.text.toString()
            val addressOfOreder:String = mAlertDialog.addreesOfOreder.text.toString()
            if(addressOfOreder.isEmpty()||quantity.isEmpty()){
                Toast.makeText(this,"some of the entities are empty",Toast.LENGTH_SHORT).show()
            }else{
                GlobalScope.launch {
                    val userDao = UserDao()
                    Log.d("checking", "$uid here i can come21")
                    val user = userDao.getUserById(uid).await().toObject(User::class.java)!!
                    val order = Order(user,nameOfOrder, quantity, addressOfOreder)
                    shopDao.placeOrder(shopId, order)
                }
                Toast.makeText(this,"Ordered successfully ",Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
                val ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child(shopId).child("notificationKey");
                ref.addValueEventListener(
                    object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            Log.d("DataBaseError",error.toString())
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            sendNotification("ORDER : ","Hey you get a order for $nameOfOrder",snapshot.value.toString())
                        }

                    }
                )
            }
        }
        mAlertDialog.cancel_button.setOnClickListener {
            Log.d("checking","here i can come5")
            mAlertDialog.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true;
    }

    override fun OnCartCliked(item: item) {
        val cartDao = CartDao()
        Toast.makeText(this, "Added to Cart ", Toast.LENGTH_SHORT).show()
        cartDao.addCart(uid,CartItem(item,shopId))
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
//                val ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child(shopId).child("notificationKey");
//                ref.addValueEventListener(
//                    object : ValueEventListener {
//                        override fun onCancelled(error: DatabaseError) {
//                            Log.d("DataBaseError",error.toString())
//                        }
//
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            sendNotification("got an order","Got Order",snapshot.value.toString())
//                        }
//
//                    }
//                )