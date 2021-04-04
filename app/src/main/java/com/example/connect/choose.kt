package com.example.connect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.socialapp.daos.UserDao
import com.example.socialapp.models.User
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_choose.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class choose : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)

        val uid:String = intent.getStringExtra("uid").toString()
        Log.d("checking","$uid")
       buyerImg.setOnClickListener {
           val intent = Intent(this,BuyerActivity::class.java)
           intent.putExtra("uid",uid)
           startActivity(intent)
       }
        sellerImg.setOnClickListener {
            val intent = Intent(this,shopActivity::class.java)
            intent.putExtra("uid",uid)
            startActivity(intent)
        }
    }
}