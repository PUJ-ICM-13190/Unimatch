package com.ajiaco.unimatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
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

        // Using Glide library to load images (make sure to add Glide dependency)
        Glide.with(this).load(user1ImageUrl).into(imageViewUser1)
        Glide.with(this).load(user2ImageUrl).into(imageViewUser2)

        // Set up message input and send button
        val editTextMessage = findViewById<EditText>(R.id.editTextMessage)
        val sendButton = findViewById<ImageView>(R.id.sendButton)

        // Function to handle sending a message
        fun sendMessage() {
            val message = editTextMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                // TODO: Implement actual chat functionality
                Toast.makeText(this, "Mensaje enviado: $message", Toast.LENGTH_SHORT).show()
                editTextMessage.text.clear()

                // Launch SwipeActivity after sending the message
                val intent = Intent(this, UniMatchSwipeActivity::class.java)
                startActivity(intent)
                finish() // Optional: close this activity if you don't want to return to it
            }
        }

        // Set click listener for send button
        sendButton.setOnClickListener { sendMessage() }

        // Set action listener for EditText's IME action (send)
        editTextMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }
}