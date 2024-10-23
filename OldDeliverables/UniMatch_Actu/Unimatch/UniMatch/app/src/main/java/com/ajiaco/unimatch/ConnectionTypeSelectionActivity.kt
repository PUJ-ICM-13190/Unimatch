package com.ajiaco.unimatch

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ajiaco.unimatch.databinding.ActivityConnectionTypeSelectionBinding

class ConnectionTypeSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConnectionTypeSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectionTypeSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar botones para cada tipo de conexión
        binding.buttonAmor.setOnClickListener {
            navigateToMatchmaking("amor")
        }

        binding.buttonAmistad.setOnClickListener {
            navigateToMatchmaking("amistad")
        }

        binding.buttonNetworking.setOnClickListener {
            navigateToMatchmaking("networking")
        }
    }

    private fun navigateToMatchmaking(type: String) {
        // Aquí puedes agregar lógica adicional para pasar información del tipo de conexión seleccionada
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("connection_type", type)
        startActivity(intent)
        finish() // Finalizar esta actividad para que no se pueda volver atrás fácilmente
    }
}
