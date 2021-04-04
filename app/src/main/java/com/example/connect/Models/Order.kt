package com.example.connect.Models

import com.example.socialapp.models.User

data class Order(
    val orderBy: User = User(),
    val name:String = "",
    val quantity:String="",
    val address:String = ""
)