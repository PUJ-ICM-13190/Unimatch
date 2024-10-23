package com.ajiaco.unimatch

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ajiaco.unimatch.databinding.ActivityChatsGrupalesBinding

class ChatsGrupales : AppCompatActivity() {

    private lateinit var binding: ActivityChatsGrupalesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatsGrupalesBinding.inflate(layoutInflater)
        setContentView(binding.root)



        }
    }
