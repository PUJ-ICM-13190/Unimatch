package com.ajiaco.unimatch.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ajiaco.unimatch.ConnectionTypeSelectionActivity
import com.ajiaco.unimatch.MainActivity
import com.ajiaco.unimatch.MatchmakingActivity
import com.ajiaco.unimatch.databinding.ActivityProfileCreationBinding

class ProfileCreationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileCreationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botón para seleccionar imagen de perfil
        binding.buttonSelectProfilePicture.setOnClickListener {
            // Lógica para seleccionar una foto (esto puede hacerse con Intent a la galería)
            Toast.makeText(this, "Seleccionar imagen no implementado", Toast.LENGTH_SHORT).show()
        }

        // Botón para guardar el perfil y continuar
        binding.buttonSaveProfile.setOnClickListener {
            val name = binding.inputName.text.toString()
            val university = binding.inputUniversity.text.toString()
            val major = binding.inputMajor.text.toString()

            if (name.isNotEmpty() && university.isNotEmpty() && major.isNotEmpty()) {
                // Lógica para guardar la información del perfil

                // Navegación a la siguiente pantalla (Selección de Tipo de Conexión)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("navigate_to", "matchmaking")  // Si quieres pasar algún extra opcional
                startActivity(intent)
                finish()  // Finaliza esta actividad para que no pueda regresar fácilmente
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
