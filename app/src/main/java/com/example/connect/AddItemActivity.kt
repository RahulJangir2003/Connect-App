package com.example.connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.connect.Daos.ShopDao
import com.example.connect.Models.Shop
import com.example.connect.Models.item
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.activity_create_shop.*

class AddItemActivity : AppCompatActivity() {
    lateinit var shopDao: ShopDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        val shopId:String = intent.getStringExtra("shopId").toString()
        shopDao = ShopDao()
        addItem.setOnClickListener {
            val nameOfItem:String = nameOfItem.text.toString().trim()
            val price:String = priceOfItem.text.toString().trim()
            val about:String = aboutItem.text.toString().trim()
            if(nameOfItem.isEmpty()||price.isEmpty()){
                Toast.makeText(this,"some of the field is empty", Toast.LENGTH_SHORT).show()
            }else{
                val item = item(nameOfItem,price,about)
                shopDao.updateShopItem(shopId,item)
                finish()
            }
        }
    }
}