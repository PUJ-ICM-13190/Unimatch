package com.ajiaco.unimatch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ajiaco.unimatch.databinding.ActivityPersonalProfileBinding

class PersonalProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonalProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración del botón "Edit your profile"
        binding.btnEditProfile.setOnClickListener {
            // Navegar a la actividad EditProfileActivity
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnUpgrade.setOnClickListener {
            try {
                val intent = Intent(this, PremiumSubscriptionActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                // Agregar un log para ver el error
                Log.e("PersonalProfile", "Error launching activity: ${e.message}")
            }
        }
    }
}