package com.ajiaco.unimatch.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ajiaco.unimatch.databinding.ActivityVerificationBinding

class VerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Acción al presionar el botón de reenviar correo
        binding.buttonResendEmail.setOnClickListener {
            // Lógica para reenviar el correo de verificación
        }

        // Simulamos que la verificación fue exitosa y redirigimos al perfil
        // En un escenario real, esto debería ser activado cuando el usuario verifique su correo
        binding.buttonContinueToProfile.setOnClickListener {
            startActivity(Intent(this, ProfileCreationActivity::class.java))
        }
    }
}
