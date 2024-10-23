package com.ajiaco.unimatch

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajiaco.unimatch.databinding.ItemChatBinding
import com.bumptech.glide.Glide

class ChatsAdapter (private val chats: List<Chat>) : RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>(){

    // Crea nuevas vistas
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    // Reemplaza el contenido de una vista
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]
        holder.bind(chat)
    }

    // Devuelve el tamaño de la lista de chats
    override fun getItemCount(): Int = chats.size

    // ViewHolder para cada chat
    class ChatViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.chatUserName.text = chat.userName
            binding.chatLastMessage.text = chat.lastMessage
            // Usa Glide u otra librería para cargar la imagen de perfil
            Glide.with(binding.root.context).load(chat.userProfilePicUrl).into(binding.chatUserImage)
            binding.root.setOnClickListener {
                // Crear un intent para redirigir a la actividad de detalles del chat
                val context = binding.root.context
                val intent = Intent(context, ChatDetailActivity::class.java).apply {
                    putExtra("CHAT_USER_NAME", chat.userName)
                    putExtra("CHAT_USER_IMAGE_URL", chat.userProfilePicUrl)
                    putExtra("CHAT_IMAGE_URL", chat.userProfilePicUrl)
                }
                context.startActivity(intent)  // Iniciar la actividad de detalles del chat
            }
        }
    }
}