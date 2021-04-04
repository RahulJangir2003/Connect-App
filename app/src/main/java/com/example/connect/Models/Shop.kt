package com.example.connect.Models

import com.example.socialapp.models.User
import java.io.Serializable

data class Shop(
    val userId:String="",
    val typeOfShop:String = "",
    val nameOfShop:String = "",
    val createdBy: User = User(),
    val createdAt: Long = 0L,
    val address:String = "",
    val items: ArrayList<item> = ArrayList(),
    val orders:ArrayList<Order> = ArrayList()
)