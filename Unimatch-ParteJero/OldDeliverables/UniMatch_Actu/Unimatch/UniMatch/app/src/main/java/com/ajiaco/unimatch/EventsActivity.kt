package com.ajiaco.unimatch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        recyclerView = findViewById(R.id.eventsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val events = listOf(
            Event("Curso grupal de acuarela", "Únete para aprender a pintar en acuarela junto a otros estudiantes", R.drawable.acuarela, Date(2024, 6, 15)),
            Event("Tech Meetup", "Haz networking con profesionales de la tecnología", R.drawable.tech_meetup, Date(2024, 7, 1)),
            Event("Caminata Ecológica", "Descubre el Páramo de Matarredonda y haz nuevos amigos!", R.drawable.caminata, Date(2024, 7, 20))
            // Add more events as needed
        )

        eventAdapter = EventAdapter(events)
        recyclerView.adapter = eventAdapter
    }
}

data class Event(val name: String, val details: String, val imageResId: Int, val date: Date)

class EventAdapter(private val events: List<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventImage: ImageView = view.findViewById(R.id.eventImage)
        val eventName: TextView = view.findViewById(R.id.eventName)
        val eventDetails: TextView = view.findViewById(R.id.eventDetails)
        val eventDate: TextView = view.findViewById(R.id.eventDate)
        val joinButton: Button = view.findViewById(R.id.joinEventButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_card, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.eventImage.setImageResource(event.imageResId)
        holder.eventName.text = event.name
        holder.eventDetails.text = event.details

        // Format and set the date
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        holder.eventDate.text = dateFormat.format(event.date)

        holder.joinButton.setOnClickListener {
            // Handle join event action
        }
    }

    override fun getItemCount() = events.size
}