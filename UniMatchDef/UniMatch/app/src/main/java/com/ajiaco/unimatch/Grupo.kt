// Grupo.kt
package com.ajiaco.unimatch

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Grupo(
    val id: String,
    val nombre: String,
    val facultad: String,
    val carrera: String,
    val descripcion: String,
    val imagenUrl: String,
    val numMiembros: Int,
    val tipo: String, // Académico, Deportivo, Cultural, etc.
    val requisitos: String?,
    val administrador: String,
    val enlaces: Map<String, String>? // Para enlaces a Discord, WhatsApp, etc.
) : Parcelable