package com.ajiaco.unimatch

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Evento(
    val id: String,
    val titulo: String,
    val descripcion: String?,
    val fecha: String,
    val lugar: String,
    val precio: Double?,
    val imagenUrl: String?,
    val latitud: Double,
    val longitud: Double
) : Parcelable