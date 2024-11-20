package com.ajiaco.unimatch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ajiaco.unimatch.databinding.ActivityPerfilDetalladoBinding

class PerfilDetalladoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilDetalladoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilDetalladoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el perfil del intent
        val profile = intent.getSerializableExtra("profile") as? Profile

        // Si el perfil no es nulo, mostrar los datos
        profile?.let {
            // Información básica
            binding.nameAgeTextView.text = "${it.name}, ${it.age}"
            binding.locationTextView.text = it.location
            binding.bioTextView.text = it.bio

            // Información detallada
            val details = StringBuilder()
            if (it.education.isNotBlank()) details.append("Educación: ${it.education}\n")
            if (it.relationshipStatus.isNotBlank()) details.append("Estado: ${it.relationshipStatus}\n")
            if (it.height.isNotBlank()) details.append("Altura: ${it.height}\n")
            if (it.gender.isNotBlank()) details.append("Género: ${it.gender}\n")
            if (it.smoking.isNotBlank()) details.append("Fumar: ${it.smoking}\n")
            if (it.location.isNotBlank()) details.append("Ubicación: ${it.location}")

            binding.detailsTextView.text = details.toString()

            // Cargar imagen si existe
            if (it.imageUrl.isNotBlank()) {
                Glide.with(this)
                    .load(it.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_default_user)
                    .error(R.drawable.ic_default_user)
                    .into(binding.profileImageView)
            } else {
                binding.profileImageView.setImageResource(R.drawable.ic_default_user)
            }

            // Mostrar intereses si existen
            if (it.interests.isNotEmpty()) {
                binding.interestsTextView.text = "Intereses: ${it.interests.joinToString(", ")}"
            }
        }
    }
}