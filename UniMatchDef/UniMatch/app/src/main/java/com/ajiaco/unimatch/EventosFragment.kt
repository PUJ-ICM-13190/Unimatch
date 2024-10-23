package com.ajiaco.unimatch

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException

class EventosFragment : Fragment() {
    private lateinit var eventosRecyclerView: RecyclerView
    private lateinit var nuevosEventosRecyclerView: RecyclerView
    private lateinit var eventosAdapter: EventosAdapter
    private lateinit var nuevosEventosAdapter: NuevosEventosAdapter

    private val eventoDetalleResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // Recargar ambas listas
            loadEventos()
            loadNuevosEventos()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_eventos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews(view)
        loadEventos()
        loadNuevosEventos()
    }

    private fun setupRecyclerViews(view: View) {
        // Setup para eventos actuales (horizontal)
        eventosRecyclerView = view.findViewById(R.id.eventosRecyclerView)
        eventosRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        eventosAdapter = EventosAdapter()
        eventosRecyclerView.adapter = eventosAdapter

        // Setup para nuevos eventos (vertical)
        nuevosEventosRecyclerView = view.findViewById(R.id.nuevosEventosRecyclerView)
        nuevosEventosRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        nuevosEventosAdapter = NuevosEventosAdapter()
        nuevosEventosRecyclerView.adapter = nuevosEventosAdapter

        // Configurar click listeners
        eventosAdapter.setOnEventoClickListener(object : EventosAdapter.OnEventoClickListener {
            override fun onEventoClick(evento: Evento) {
                abrirDetalleEvento(evento, false)
            }
        })

        nuevosEventosAdapter.setOnEventoClickListener(object : NuevosEventosAdapter.OnEventoClickListener {
            override fun onEventoClick(evento: Evento) {
                abrirDetalleEvento(evento, true)
            }
        })
    }

    private fun loadEventos() {
        try {
            val eventos = if (File(requireContext().filesDir, "eventos.json").exists()) {
                // Leer desde el archivo guardado
                val jsonString = File(requireContext().filesDir, "eventos.json").readText()
                val gson = Gson()
                val eventosType = object : TypeToken<List<Evento>>() {}.type
                gson.fromJson<List<Evento>>(jsonString, eventosType)
            } else {
                // Leer desde assets (primera vez)
                val jsonString = requireContext().assets.open("eventos.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val eventosType = object : TypeToken<List<Evento>>() {}.type
                gson.fromJson<List<Evento>>(jsonString, eventosType)
            }
            eventosAdapter.submitList(eventos)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadNuevosEventos() {
        try {
            val nuevosEventos = if (File(requireContext().filesDir, "nuevos_eventos.json").exists()) {
                // Leer desde el archivo guardado
                val jsonString = File(requireContext().filesDir, "nuevos_eventos.json").readText()
                val gson = Gson()
                val eventosType = object : TypeToken<List<Evento>>() {}.type
                gson.fromJson<List<Evento>>(jsonString, eventosType)
            } else {
                // Leer desde assets (primera vez)
                val jsonString = requireContext().assets.open("nuevos_eventos.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val eventosType = object : TypeToken<List<Evento>>() {}.type
                gson.fromJson<List<Evento>>(jsonString, eventosType)
            }
            nuevosEventosAdapter.submitList(nuevosEventos)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun abrirDetalleEvento(evento: Evento, esNuevoEvento: Boolean) {
        val intent = Intent(requireContext(), EventoDetalleActivity::class.java).apply {
            putExtra("evento", evento)
            putExtra("esNuevoEvento", esNuevoEvento)
        }
        eventoDetalleResult.launch(intent)
    }
}