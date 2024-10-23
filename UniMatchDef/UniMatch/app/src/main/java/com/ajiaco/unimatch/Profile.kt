package com.ajiaco.unimatch

import java.io.Serializable

data class Profile(
    val id: Int,
    val name: String,
    val age: Int,
    val bio: String,
    val gender: String,
    val distance: Int,
    val interests: List<String>,
    val imageUrl: String,
    val location: String,             // Añadido
    val height: String,               // Añadido
    val smoking: String,              // Añadido
    val sign: String,                 // Añadido
    val relationshipStatus: String,   // Añadido
    val children: String,             // Añadido
    val petLover: String,             // Añadido
    val education: String,            // Añadido
    val languages: List<String>       // Añadido
): Serializable
