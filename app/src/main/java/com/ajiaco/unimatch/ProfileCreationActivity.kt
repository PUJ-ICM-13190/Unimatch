package com.ajiaco.unimatch.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ajiaco.unimatch.MainActivity
import com.ajiaco.unimatch.Profile
import com.ajiaco.unimatch.databinding.ActivityProfileCreationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class ProfileCreationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileCreationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference  // Referencia a la base de datos de Firebase
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private val PICK_IMAGE_REQUEST = 71  // Código para el intent de selección de imagen

    private var profileImageUri: String = ""  // Almacenar la URL de la imagen seleccionada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("profiles")
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        val email = auth.currentUser?.email ?: ""

        binding.buttonSelectProfilePicture.setOnClickListener {
            // Intent para seleccionar una imagen de la galería
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.btnSaveProfile.setOnClickListener {
            val name = binding.inputName.text.toString().trim()
            val age = binding.inputAge.text.toString().toIntOrNull() ?: 0
            val bio = binding.inputBio.text.toString().trim()
            val gender = binding.inputGender.text.toString().trim()
            val location = binding.inputLocation.text.toString().trim()
            val height = binding.inputHeight.text.toString().trim()
            val smoking = binding.inputSmoking.text.toString().trim()
            val relationshipStatus = binding.inputRelationshipStatus.text.toString().trim()
            val education = binding.inputEducation.text.toString().trim()

            // Crear un perfil con la URL de la imagen
            val profile = Profile(
                id = 0,  // Asignar un valor adecuado
                email = email,
                name = name,
                age = age,
                bio = bio,
                gender = gender,
                distance = location.toIntOrNull() ?: 0,
                interests = listOf(),
                imageUrl = profileImageUri,  // Aquí se usa la URL de la imagen
                location = location,
                height = height,
                smoking = smoking,
                sign = "",
                relationshipStatus = relationshipStatus,
                children = "",
                petLover = "",
                education = education,
                languages = listOf()
            )

            // Guardar el perfil en Firebase Database
            val userId = auth.currentUser?.uid
            if (userId != null) {
                database.child(userId).setValue(profile)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Perfil guardado exitosamente", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Error al guardar el perfil", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    // Callback para manejar la selección de imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            binding.profileImageView.setImageURI(imageUri)  // Mostrar la imagen en la UI

            // Subir la imagen seleccionada a Firebase Storage
            uploadImageToFirebase(imageUri)
        }
    }

    // Subir la imagen seleccionada a Firebase Storage
    private fun uploadImageToFirebase(imageUri: Uri?) {
        if (imageUri != null) {
            val fileReference = storageReference.child("profile_pictures/${UUID.randomUUID()}.jpg")
            fileReference.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    fileReference.downloadUrl.addOnSuccessListener { uri ->
                        profileImageUri = uri.toString()  // Guardar la URL de la imagen
                        Toast.makeText(this, "Imagen subida con éxito", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
                    Log.d("Firebase", "Imagen subida con éxito: $profileImageUri")
                }
        }
    }
}
