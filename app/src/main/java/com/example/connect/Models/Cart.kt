package com.example.connect.Models

data class Cart(
    val uid:String = "",
    val cartList:ArrayList<CartItem>  = ArrayList()
)