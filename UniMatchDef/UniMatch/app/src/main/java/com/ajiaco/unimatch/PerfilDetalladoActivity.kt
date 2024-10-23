package com.ajiaco.unimatch

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.ajiaco.unimatch.databinding.ActivityPerfilDetalladoBinding  // Importa el binding generado

class PerfilDetalladoActivity : AppCompatActivity() {

    // Declaramos el binding
    private lateinit var binding: ActivityPerfilDetalladoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflamos el layout usando ViewBinding
        binding = ActivityPerfilDetalladoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajustar los insets (edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Recibir el perfil seleccionado desde el intent
        val profile = intent.getSerializableExtra("profile") as? Profile

        // Si el perfil no es nulo, cargar los datos en la UI
        profile?.let {
            // Usar ViewBinding para acceder a las vistas
            binding.nameAgeTextView.text = "${it.name}, ${it.age}"
            binding.locationTextView.text = it.location
            binding.bioTextView.text = it.bio
            binding.interestsTextView.text = it.interests.joinToString(", ")
            binding.detailsTextView.text = """
                Altura: ${it.height}
                Fumador: ${it.smoking}
                Signo: ${it.sign}
                Relación: ${it.relationshipStatus}
                Hijos: ${it.children}
                ${it.petLover}
                Educación: ${it.education}
            """.trimIndent()
            binding.languagesTextView.text = it.languages.joinToString(", ")

            // Cargar la imagen de perfil usando Glide
            Glide.with(this)
                .load(it.imageUrl)
                .centerCrop()
                .into(binding.profileImageView)
           }
      }
}