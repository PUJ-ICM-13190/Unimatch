package com.ajiaco.unimatch


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException

class GruposFragment : Fragment() {
    private lateinit var gruposRecyclerView: RecyclerView
    private lateinit var nuevosGruposRecyclerView: RecyclerView
    private lateinit var gruposAdapter: GruposAdapter
    private lateinit var nuevosGruposAdapter: NuevosGruposAdapter

    private val grupoDetalleResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            loadGrupos()
            loadNuevosGrupos()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_grupos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews(view)
        loadGrupos()
        loadNuevosGrupos()
    }

    private fun setupRecyclerViews(view: View) {
        gruposRecyclerView = view.findViewById(R.id.gruposRecyclerView)
        gruposRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        gruposAdapter = GruposAdapter()
        gruposRecyclerView.adapter = gruposAdapter

        nuevosGruposRecyclerView = view.findViewById(R.id.nuevosGruposRecyclerView)
        nuevosGruposRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        nuevosGruposAdapter = NuevosGruposAdapter()
        nuevosGruposRecyclerView.adapter = nuevosGruposAdapter

        gruposAdapter.setOnGrupoClickListener(object : GruposAdapter.OnGrupoClickListener {
            override fun onGrupoClick(grupo: Grupo) {
                abrirDetalleGrupo(grupo, false)
            }
        })

        nuevosGruposAdapter.setOnGrupoClickListener(object : NuevosGruposAdapter.OnGrupoClickListener {
            override fun onGrupoClick(grupo: Grupo) {
                abrirDetalleGrupo(grupo, true)
            }
        })
    }

    private fun loadGrupos() {
        try {
            val grupos = if (File(requireContext().filesDir, "grupos.json").exists()) {
                val jsonString = File(requireContext().filesDir, "grupos.json").readText()
                val gson = Gson()
                val gruposType = object : TypeToken<List<Grupo>>() {}.type
                gson.fromJson<List<Grupo>>(jsonString, gruposType)
            } else {
                val jsonString = requireContext().assets.open("grupos.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val gruposType = object : TypeToken<List<Grupo>>() {}.type
                gson.fromJson<List<Grupo>>(jsonString, gruposType)
            }
            gruposAdapter.submitList(grupos)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadNuevosGrupos() {
        try {
            val nuevosGrupos = if (File(requireContext().filesDir, "nuevos_grupos.json").exists()) {
                val jsonString = File(requireContext().filesDir, "nuevos_grupos.json").readText()
                val gson = Gson()
                val gruposType = object : TypeToken<List<Grupo>>() {}.type
                gson.fromJson<List<Grupo>>(jsonString, gruposType)
            } else {
                val jsonString = requireContext().assets.open("nuevos_grupos.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val gruposType = object : TypeToken<List<Grupo>>() {}.type
                gson.fromJson<List<Grupo>>(jsonString, gruposType)
            }
            nuevosGruposAdapter.submitList(nuevosGrupos)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun abrirDetalleGrupo(grupo: Grupo, esNuevoGrupo: Boolean) {
        val intent = Intent(requireContext(), GrupoDetalleActivity::class.java).apply {
            putExtra("grupo", grupo)
            putExtra("esNuevoGrupo", esNuevoGrupo)
        }
        grupoDetalleResult.launch(intent)
    }
}

