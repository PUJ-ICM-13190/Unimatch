package com.ajiaco.unimatch

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ajiaco.unimatch.databinding.ActivitySplashBinding
import com.ajiaco.unimatch.ui.OnboardingActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val PREFS_NAME = "unimatch_prefs"
    private val FIRST_TIME_KEY = "is_first_time"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar si es la primera vez que se ejecuta la app
        val sharedPreferences: SharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean(FIRST_TIME_KEY, true)

        binding.root.postDelayed({
            if (isFirstTime) {
                // Si es la primera vez, mostrar Onboarding
                val editor = sharedPreferences.edit()
                editor.putBoolean(FIRST_TIME_KEY, false)
                editor.apply()

                startActivity(Intent(this, OnboardingActivity::class.java))
            } else {
                // Si ya no es la primera vez, ir a la pantalla principal
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }, 5000) // Retraso de 5 segundos
    }
}
