package com.ajiaco.unimatch

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ajiaco.unimatch.MessageAdapter
import com.ajiaco.unimatch.R



class ChatDetailActivity : AppCompatActivity() {

    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var editTextMessage: EditText
    private lateinit var sendButton: Button

    // Lista de mensajes en el chat
    private val messages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_detail)

        // Recuperar el nombre del usuario y la URL de la imagen desde el intent
        val userName = intent.getStringExtra("CHAT_USER_NAME")
        val userImageUrl = intent.getStringExtra("CHAT_USER_IMAGE_URL")

        // Configurar las vistas
        val imageViewUser = findViewById<ImageView>(R.id.chatDetailUserImage)
        val textViewUserName = findViewById<TextView>(R.id.chatDetailUserName)
        textViewUserName.text = userName

        // Cargar la imagen de perfil usando Glide
        Glide.with(this)
            .load(userImageUrl)
            .placeholder(R.drawable.ic_default_user)
            .error(R.drawable.ic_default_user)
            .into(imageViewUser)

        // Configuración del RecyclerView
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(messages)
        messagesRecyclerView.adapter = messageAdapter

        // Recuperar el mensaje previo que fue enviado en SuccessfulMatchActivity
        val previousMessage = intent.getStringExtra("CHAT_PREVIOUS_MESSAGE")
        previousMessage?.let {
            messages.add(it)  // Añadir el mensaje previo al inicio de la conversación
        }

        // Configurar el campo de texto y el botón de envío
        editTextMessage = findViewById(R.id.editTextMessage)
        sendButton = findViewById(R.id.sendButton)

        // Manejar el envío de un nuevo mensaje
        sendButton.setOnClickListener {
            val newMessage = editTextMessage.text.toString().trim()
            if (newMessage.isNotEmpty()) {
                messages.add(newMessage)  // Añadir el nuevo mensaje a la lista
                messageAdapter.notifyItemInserted(messages.size - 1)  // Notificar al adaptador
                messagesRecyclerView.scrollToPosition(messages.size - 1)  // Desplazar hacia el último mensaje
                editTextMessage.text.clear()  // Limpiar el campo de texto
            }
        }
    }
}