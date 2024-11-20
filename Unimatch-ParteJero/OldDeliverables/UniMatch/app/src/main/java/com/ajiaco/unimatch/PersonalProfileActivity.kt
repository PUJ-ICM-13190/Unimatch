package com.ajiaco.unimatch

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ajiaco.unimatch.databinding.ActivityPersonalProfileBinding

class PersonalProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        }
    }
