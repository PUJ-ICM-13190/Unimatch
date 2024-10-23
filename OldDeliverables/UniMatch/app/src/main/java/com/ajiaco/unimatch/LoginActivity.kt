package com.ajiaco.unimatch.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ajiaco.unimatch.ConnectionTypeSelectionActivity
import com.ajiaco.unimatch.MainActivity
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
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Simulación de inicio de sesión. Aquí podrías agregar validación real
                startActivity(Intent(this, ConnectionTypeSelectionActivity::class.java))
                finish() // Finaliza la actividad para que no se pueda volver atrás fácilmente
            } else {
                // Mensaje de error si los campos están vacíos
                Toast.makeText(this, "Por favor, ingrese su correo y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
