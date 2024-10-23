package com.ajiaco.unimatch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject

class EventoDetalleActivity : AppCompatActivity() {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormatter = SimpleDateFormat("dd MMM yyyy", Locale("es"))
    private lateinit var suscribirmeButton: MaterialButton
    private var evento: Evento? = null
    private var esNuevoEvento: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evento_detalle)

        evento = intent.getParcelableExtra<Evento>("evento")
            ?: throw IllegalArgumentException("Debe proporcionar un evento")
        esNuevoEvento = intent.getBooleanExtra("esNuevoEvento", false)

        setupToolbar()
        setupEventoDetails(evento!!)
        setupSuscripcionButton()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupEventoDetails(evento: Evento) {
        // Cargar imagen
        Glide.with(this)
            .load(evento.imagenUrl)
            .centerCrop()
            .into(findViewById(R.id.eventoImageView))

        // Establecer título
        findViewById<TextView>(R.id.tituloEventoTextView).text = evento.titulo

        // Formatear y mostrar fecha
        try {
            val date = dateFormatter.parse(evento.fecha)
            findViewById<Chip>(R.id.fechaChip).text = date?.let { displayDateFormatter.format(it) }
        } catch (e: Exception) {
            findViewById<Chip>(R.id.fechaChip).text = evento.fecha
        }

        // Establecer lugar
        findViewById<Chip>(R.id.lugarChip).text = evento.lugar

        // Establecer descripción
        findViewById<TextView>(R.id.descripcionTextView).text = evento.descripcion

        // Establecer precio
        findViewById<TextView>(R.id.precioTextView).text = evento.precio?.let {
            String.format("%.2f €", it)
        } ?: "Gratis"

        // Configurar botón de ubicación
        findViewById<MaterialButton>(R.id.ubicacionButton).setOnClickListener {
            abrirMapa(evento)
        }
    }

    private fun setupSuscripcionButton() {
        suscribirmeButton = findViewById(R.id.suscribirmeButton)

        if (esNuevoEvento) {
            suscribirmeButton.visibility = View.VISIBLE
            suscribirmeButton.setOnClickListener {
                suscribirseAEvento()
            }
        } else {
            suscribirmeButton.visibility = View.GONE
        }
    }

    private fun suscribirseAEvento() {
        evento?.let { eventoActual ->
            try {
                // 1. Leer eventos actuales
                val eventosActuales = leerEventosActuales()

                // 2. Añadir el nuevo evento
                eventosActuales.add(eventoActual)

                // 3. Guardar eventos actualizados
                guardarEventosActuales(eventosActuales)

                // 4. Eliminar de nuevos eventos
                eliminarDeNuevosEventos(eventoActual)

                // 5. Mostrar confirmación
                Toast.makeText(this, "¡Te has suscrito al evento!", Toast.LENGTH_SHORT).show()

                // 6. Actualizar UI
                suscribirmeButton.visibility = View.GONE

                // 7. Refrescar la actividad principal
                setResult(RESULT_OK)
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Error al suscribirse al evento", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun leerEventosActuales(): MutableList<Evento> {
        val jsonString = assets.open("eventos.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val eventosType = object : TypeToken<List<Evento>>() {}.type
        return gson.fromJson<List<Evento>>(jsonString, eventosType).toMutableList()
    }

    private fun guardarEventosActuales(eventos: List<Evento>) {
        val gson = Gson()
        val jsonString = gson.toJson(eventos)
        val file = File(filesDir, "eventos.json")
        FileWriter(file).use { it.write(jsonString) }
    }

    private fun eliminarDeNuevosEventos(eventoAEliminar: Evento) {
        val nuevosEventos = leerNuevosEventos().filter { it.id != eventoAEliminar.id }
        val gson = Gson()
        val jsonString = gson.toJson(nuevosEventos)
        val file = File(filesDir, "nuevos_eventos.json")
        FileWriter(file).use { it.write(jsonString) }
    }

    private fun leerNuevosEventos(): List<Evento> {
        val jsonString = assets.open("nuevos_eventos.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val eventosType = object : TypeToken<List<Evento>>() {}.type
        return gson.fromJson(jsonString, eventosType)
    }

    private fun abrirMapa(evento: Evento) {
        val intent = Intent(this, MapaEventoActivity::class.java).apply {
            putExtra("latitud", evento.latitud)
            putExtra("longitud", evento.longitud)
            putExtra("titulo", evento.titulo)
            putExtra("lugar", evento.lugar)
        }
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}