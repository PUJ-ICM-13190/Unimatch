package com.ajiaco.unimatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class NuevosEventosAdapter : ListAdapter<Evento, NuevosEventosAdapter.EventoViewHolder>(EventoDiffCallback()) {

    interface OnEventoClickListener {
        fun onEventoClick(evento: Evento)
    }

    private var onEventoClickListener: OnEventoClickListener? = null

    fun setOnEventoClickListener(listener: OnEventoClickListener) {
        onEventoClickListener = listener
    }

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormatter = SimpleDateFormat("dd MMM yyyy", Locale("es"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nuevo_evento, parent, false)
        return EventoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = getItem(position)
        holder.bind(evento, dateFormatter, displayDateFormatter, onEventoClickListener)
    }

    class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.eventoImageView)
        private val tituloTextView: TextView = itemView.findViewById(R.id.nombreEventoTextView)
        private val fechaTextView: TextView = itemView.findViewById(R.id.fechaEventoTextView)
        private val lugarTextView: TextView = itemView.findViewById(R.id.lugarEventoTextView)
        private val precioTextView: TextView = itemView.findViewById(R.id.precioEventoTextView)

        fun bind(
            evento: Evento,
            dateFormatter: SimpleDateFormat,
            displayDateFormatter: SimpleDateFormat,
            clickListener: OnEventoClickListener?
        ) {
            if (!evento.imagenUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(evento.imagenUrl)
                    .centerCrop()
                    .into(imageView)
            }

            tituloTextView.text = evento.titulo

            try {
                val date = dateFormatter.parse(evento.fecha)
                fechaTextView.text = date?.let { displayDateFormatter.format(it) }
            } catch (e: Exception) {
                fechaTextView.text = evento.fecha
            }

            lugarTextView.text = evento.lugar
            precioTextView.text = evento.precio?.let {
                String.format("%.2f €", it)
            } ?: "Gratis"

            itemView.setOnClickListener {
                clickListener?.onEventoClick(evento)
            }
        }
    }
}