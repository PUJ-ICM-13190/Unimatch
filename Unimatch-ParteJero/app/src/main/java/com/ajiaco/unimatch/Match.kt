package com.ajiaco.unimatch

import com.google.firebase.database.IgnoreExtraProperties
import java.util.Date

@IgnoreExtraProperties
data class Match(
    val id: String = "", // ID único del match
    val user1Id: String = "", // ID del primer usuario
    val user2Id: String = "", // ID del segundo usuario
    val user1Liked: Boolean = false, // Si el primer usuario dio like
    val user2Liked: Boolean = false, // Si el segundo usuario dio like
    val isMatch: Boolean = false, // True si ambos usuarios dieron like
    val timestamp: Long = Date().time, // Timestamp de cuando se creó el match
    val lastMessage: String = "", // Último mensaje enviado
    val lastMessageTimestamp: Long = 0 // Timestamp del último mensaje
)