package com.ajiaco.unimatch

data class Profile(
    val id: Int,
    val name: String,
    val age: Int,
    val bio: String,
    val gender: String,
    val distance: Int,
    val interests: List<String>,
    val imageUrl: String
)
