package com.ajiaco.unimatch

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileWriter

class GrupoDetalleActivity : AppCompatActivity() {
    private lateinit var grupo: Grupo
    private var esNuevoGrupo: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupo_detalle)

        grupo = intent.getParcelableExtra("grupo")!!
        esNuevoGrupo = intent.getBooleanExtra("esNuevoGrupo", false)

        setupToolbar()
        cargarInformacionGrupo()
        setupUnirseButton()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun cargarInformacionGrupo() {
        // Cargar imagen
        Glide.with(this)
            .load(grupo.imagenUrl)
            .centerCrop()
            .into(findViewById(R.id.grupoImageView))

        // Información básica
        findViewById<TextView>(R.id.nombreGrupoTextView).text = grupo.nombre
        findViewById<TextView>(R.id.facultadTextView).text = "${grupo.facultad} • ${grupo.carrera}"
        findViewById<TextView>(R.id.descripcionTextView).text = grupo.descripcion
        findViewById<TextView>(R.id.requisitosTextView).text = grupo.requisitos ?: "No hay requisitos específicos"
        findViewById<TextView>(R.id.adminTextView).text = "Administrador: ${grupo.administrador}"
        findViewById<TextView>(R.id.miembrosTextView).text = "${grupo.numMiembros} miembros"

        // Configurar chip de tipo
        findViewById<Chip>(R.id.tipoChip).apply {
            text = grupo.tipo
            setChipBackgroundColorResource(when (grupo.tipo) {
                "Académico" -> R.color.chip_academico
                "Deportivo" -> R.color.chip_deportivo
                "Cultural" -> R.color.chip_cultural
                else -> R.color.chip_default
            })
        }

        // Configurar enlaces
        val chipGroup = findViewById<com.google.android.material.chip.ChipGroup>(R.id.enlacesChipGroup)
        grupo.enlaces?.forEach { (plataforma, url) ->
            val chip = Chip(this).apply {
                text = plataforma.capitalize()
                setChipIconResource(when (plataforma.toLowerCase()) {
                    "whatsapp" -> R.drawable.ic_whatsapp
                    "discord" -> R.drawable.ic_discord
                    "instagram" -> R.drawable.ic_instagram
                    else -> R.drawable.ic_link
                })
                setOnClickListener {
                    abrirEnlace(url)
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun setupUnirseButton() {
        val unirseButton = findViewById<ExtendedFloatingActionButton>(R.id.unirseButton)

        if (!esNuevoGrupo) {
            unirseButton.text = "Ya eres miembro"
            unirseButton.isEnabled = false
            return
        }

        unirseButton.setOnClickListener {
            mostrarDialogoConfirmacion()
        }
    }

    private fun mostrarDialogoConfirmacion() {
        MaterialAlertDialogBuilder(this)
            .setTitle("¿Unirse al grupo?")
            .setMessage("¿Estás seguro de que quieres unirte a ${grupo.nombre}?")
            .setPositiveButton("Unirme") { _, _ ->
                unirseAlGrupo()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun unirseAlGrupo() {
        try {
            // Cargar grupos actuales
            val gruposFile = File(filesDir, "grupos.json")
            val grupos = if (gruposFile.exists()) {
                val jsonString = gruposFile.readText()
                val gruposType = object : TypeToken<MutableList<Grupo>>() {}.type
                Gson().fromJson<MutableList<Grupo>>(jsonString, gruposType)
            } else {
                mutableListOf()
            }

            // Agregar el nuevo grupo
            grupos.add(grupo)

            // Guardar grupos actualizados
            FileWriter(gruposFile).use { writer ->
                Gson().toJson(grupos, writer)
            }

            // Eliminar de nuevos grupos
            val nuevosGruposFile = File(filesDir, "nuevos_grupos.json")
            if (nuevosGruposFile.exists()) {
                val jsonString = nuevosGruposFile.readText()
                val nuevosGrupos = Gson().fromJson<MutableList<Grupo>>(jsonString, object : TypeToken<MutableList<Grupo>>() {}.type)
                nuevosGrupos.removeAll { it.id == grupo.id }
                FileWriter(nuevosGruposFile).use { writer ->
                    Gson().toJson(nuevosGrupos, writer)
                }
            }

            Snackbar.make(
                findViewById(android.R.id.content),
                "¡Te has unido al grupo!",
                Snackbar.LENGTH_LONG
            ).show()

            setResult(RESULT_OK)
            finish()
        } catch (e: Exception) {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Error al unirse al grupo",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun abrirEnlace(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Snackbar.make(
                findViewById(android.R.id.content),
                "No se pudo abrir el enlace",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}