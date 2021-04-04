package com.example.socialapp.models

import com.example.connect.Models.CartItem

data class User(val uid: String = "",
                val displayName: String? = "",
                val imageUrl: String = ""
)