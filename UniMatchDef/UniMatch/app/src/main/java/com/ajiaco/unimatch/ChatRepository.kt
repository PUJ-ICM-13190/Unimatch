package com.ajiaco.unimatch

object ChatRepository {

    private val chats = mutableListOf<Chat>() // Lista de chats

    // Función para agregar un nuevo chat
    fun addChat(chat: Chat) {
        chats.add(chat)
    }

    // Función para obtener todos los chats
    fun getChats(): List<Chat> {
        return chats
    }
}