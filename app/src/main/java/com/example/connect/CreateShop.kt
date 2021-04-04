package com.example.connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.connect.Daos.ShopDao
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_create_shop.*

class CreateShop : AppCompatActivity() {
    lateinit var shopDao:ShopDao
    lateinit var uid:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_shop)
        shopDao = ShopDao()
        uid = intent.getStringExtra("uid").toString()
        addShop.setOnClickListener {
            Log.d("checking"," adding in shop dao")
            val type:String = shopType.text.toString().trim()
            val name:String = shopName.text.toString().trim()
            val address:String = shopAdd.text.toString().trim()
            if(type.isEmpty()||name.isEmpty()||address.isEmpty()){
                Toast.makeText(this,"some of the field is empty",Toast.LENGTH_SHORT).show()
            }else{
                Log.d("checking","$uid adding in shop dao")
                shopDao.addShop(uid,type,name,address)
                finish()
            }
        }
    }
}