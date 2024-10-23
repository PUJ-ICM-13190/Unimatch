// NuevosGruposAdapter.kt
package com.ajiaco.unimatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView

class NuevosGruposAdapter : ListAdapter<Grupo, NuevosGruposAdapter.NuevoGrupoViewHolder>(NuevoGrupoDiffCallback()) {

    interface OnGrupoClickListener {
        fun onGrupoClick(grupo: Grupo)
    }

    private var onGrupoClickListener: OnGrupoClickListener? = null

    fun setOnGrupoClickListener(listener: OnGrupoClickListener) {
        onGrupoClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NuevoGrupoViewHolder {
        // Utilizamos el mismo layout item_grupo.xml pero con algunas modificaciones en el binding
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grupo, parent, false)

        // Modificamos el ancho de la card para que ocupe todo el ancho en la lista vertical
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        view.layoutParams = layoutParams

        return NuevoGrupoViewHolder(view)
    }

    override fun onBindViewHolder(holder: NuevoGrupoViewHolder, position: Int) {
        val grupo = getItem(position)
        holder.bind(grupo, onGrupoClickListener)
    }

    class NuevoGrupoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView as MaterialCardView
        private val imageView: ImageView = itemView.findViewById(R.id.grupoImageView)
        private val nombreTextView: TextView = itemView.findViewById(R.id.nombreGrupoTextView)
        private val facultadTextView: TextView = itemView.findViewById(R.id.facultadTextView)
        private val descripcionTextView: TextView = itemView.findViewById(R.id.descripcionGrupoTextView)
        private val miembrosTextView: TextView = itemView.findViewById(R.id.miembrosTextView)

        fun bind(grupo: Grupo, clickListener: OnGrupoClickListener?) {
            // Configurar la imagen del grupo
            if (!grupo.imagenUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(grupo.imagenUrl)
                    .centerCrop()
                    .into(imageView)
            }

            // Configurar los textos
            nombreTextView.text = grupo.nombre
            facultadTextView.text = buildString {
                append(grupo.facultad)
                if (grupo.tipo.isNotEmpty()) {
                    append(" • ")
                    append(grupo.tipo)
                }
            }
            descripcionTextView.text = grupo.descripcion
            miembrosTextView.text = "${grupo.numMiembros} miembros"

            // Configurar estilo específico para nuevos grupos
            cardView.strokeWidth = 2 // Agregar borde para destacar que es un nuevo grupo
            cardView.strokeColor = itemView.context.getColor(R.color.black)

            // Configurar el click listener
            itemView.setOnClickListener {
                clickListener?.onGrupoClick(grupo)
            }
        }
    }
}

class NuevoGrupoDiffCallback : DiffUtil.ItemCallback<Grupo>() {
    override fun areItemsTheSame(oldItem: Grupo, newItem: Grupo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Grupo, newItem: Grupo): Boolean {
        return oldItem == newItem
    }
}