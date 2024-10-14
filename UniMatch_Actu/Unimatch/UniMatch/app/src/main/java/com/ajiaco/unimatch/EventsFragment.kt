package com.ajiaco.unimatch.ui

import com.ajiaco.unimatch.Event
import com.ajiaco.unimatch.EventAdapter
import com.ajiaco.unimatch.R


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Date

class EventsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_events, container, false)

        recyclerView = view.findViewById(R.id.eventsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val events = listOf(
            Event("Curso grupal de acuarela", "Únete para aprender a pintar en acuarela junto a otros estudiantes", R.drawable.acuarela, Date(2024, 6, 15)),
            Event("Tech Meetup", "Haz networking con profesionales de la tecnología", R.drawable.tech_meetup, Date(2024, 7, 1)),
            Event("Caminata Ecológica", "Descubre el Páramo de Matarredonda y haz nuevos amigos!", R.drawable.caminata, Date(2024, 7, 20))
            // Add more events as needed
        )

        eventAdapter = EventAdapter(events)
        recyclerView.adapter = eventAdapter

        return view
    }
}