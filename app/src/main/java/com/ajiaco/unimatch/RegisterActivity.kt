package com.ajiaco.unimatch.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ajiaco.unimatch.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Al hacer clic en el botón de "Registrarse", navegamos a la pantalla de verificación de correo
        binding.buttonRegister.setOnClickListener {
            // Aquí se manejará el registro (mock por ahora)
            // Navegación hacia la pantalla de verificación de correo
            startActivity(Intent(this, VerificationActivity::class.java))
        }
    }
}
