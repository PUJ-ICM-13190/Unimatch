package com.ajiaco.unimatch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ajiaco.unimatch.databinding.ActivityMatchmakingBinding

class MatchmakingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMatchmakingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchmakingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val connectionType = intent.getStringExtra("connection_type")
        binding.textTitle.text = "Estás buscando: $connectionType"

        // Aquí agregarás la lógica para mostrar perfiles
    }
}
