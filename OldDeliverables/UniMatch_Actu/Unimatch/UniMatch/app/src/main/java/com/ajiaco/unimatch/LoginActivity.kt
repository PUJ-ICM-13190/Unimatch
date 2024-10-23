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
                // Navegación directa a MainActivity para abrir matchmaking
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("navigate_to", "matchmaking")  // Extra opcional si quieres controlar navegación
                startActivity(intent)
                finish() // Finaliza esta actividad para que no se pueda volver atrás fácilmente
            } else {
                Toast.makeText(this, "Por favor, ingrese su correo y contraseña", Toast.LENGTH_SHORT).show()
            }
        }


    }
}
