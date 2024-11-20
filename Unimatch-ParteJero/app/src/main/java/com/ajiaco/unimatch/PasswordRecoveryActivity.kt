package com.ajiaco.unimatch.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ajiaco.unimatch.LoginActivity
import com.ajiaco.unimatch.databinding.ActivityPasswordRecoveryBinding

class PasswordRecoveryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPasswordRecoveryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSendRecoveryEmail.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            // Lógica para enviar el correo de recuperación

            // Tras enviar el correo, navegamos de nuevo a la pantalla de inicio de sesión
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
