package com.ajiaco.unimatch.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ajiaco.unimatch.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navegación hacia la pantalla de recuperación de contraseña
        binding.textForgotPassword.setOnClickListener {
            startActivity(Intent(this, PasswordRecoveryActivity::class.java))
        }

        binding.buttonLogin.setOnClickListener {
            // Aquí se manejará el inicio de sesión (mock por ahora)
        }
    }
}
