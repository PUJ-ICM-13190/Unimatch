package com.ajiaco.unimatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.ajiaco.unimatch.ui.MatchmakingFragment
import com.bumptech.glide.Glide

class SuccessfulMatchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_successful_match)

        // Retrieve user image URLs from intent extras
        val user1ImageUrl = intent.getStringExtra("USER1_IMAGE_URL")
        val user2ImageUrl = intent.getStringExtra("USER2_IMAGE_URL")

        // Load images into ImageViews
        val imageViewUser1 = findViewById<ImageView>(R.id.imageViewUser1)
        val imageViewUser2 = findViewById<ImageView>(R.id.imageViewUser2)

        // Using Glide library to load images
        Glide.with(this).load(user1ImageUrl).into(imageViewUser1)
        Glide.with(this).load(user2ImageUrl).into(imageViewUser2)

        // Set up message input and send button
        val editTextMessage = findViewById<EditText>(R.id.editTextMessage)
        val sendButton = findViewById<ImageView>(R.id.sendButton)

        // Function to handle sending a message
        fun sendMessage() {
            val message = editTextMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                // Simulate sending a message
                Toast.makeText(this, "Mensaje enviado: $message", Toast.LENGTH_SHORT).show()
                editTextMessage.text.clear()
            } else {
                Toast.makeText(this, "Escribe un mensaje antes de enviar", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for send button
        sendButton.setOnClickListener {
            sendMessage()
        }

        // Set action listener for EditText's IME action (send)
        editTextMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }

        // Set click listener for "Seguir Explorando" button
        val buttonKeepExploring = findViewById<Button>(R.id.buttonKeepExploring)
        buttonKeepExploring.setOnClickListener {
            val intent = Intent(this, MatchmakingFragment::class.java) // Asegúrate de usar el nombre correcto de la Activity
            startActivity(intent)
            finish() // Opcional: cerrar la SuccessfulMatchActivity
        }
    }
}
