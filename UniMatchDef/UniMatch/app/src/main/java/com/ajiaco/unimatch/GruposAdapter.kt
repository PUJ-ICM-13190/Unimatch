// GruposAdapter.kt
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

class GruposAdapter : ListAdapter<Grupo, GruposAdapter.GrupoViewHolder>(GrupoDiffCallback()) {

    interface OnGrupoClickListener {
        fun onGrupoClick(grupo: Grupo)
    }

    private var onGrupoClickListener: OnGrupoClickListener? = null

    fun setOnGrupoClickListener(listener: OnGrupoClickListener) {
        onGrupoClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrupoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grupo, parent, false)
        return GrupoViewHolder(view)
    }

    override fun onBindViewHolder(holder: GrupoViewHolder, position: Int) {
        val grupo = getItem(position)
        holder.bind(grupo, onGrupoClickListener)
    }

    class GrupoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.grupoImageView)
        private val nombreTextView: TextView = itemView.findViewById(R.id.nombreGrupoTextView)
        private val facultadTextView: TextView = itemView.findViewById(R.id.facultadTextView)
        private val descripcionTextView: TextView = itemView.findViewById(R.id.descripcionGrupoTextView)
        private val miembrosTextView: TextView = itemView.findViewById(R.id.miembrosTextView)

        fun bind(grupo: Grupo, clickListener: OnGrupoClickListener?) {
            if (!grupo.imagenUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(grupo.imagenUrl)
                    .centerCrop()
                    .into(imageView)
            }

            nombreTextView.text = grupo.nombre
            facultadTextView.text = grupo.facultad
            descripcionTextView.text = grupo.descripcion
            miembrosTextView.text = "${grupo.numMiembros} miembros"

            itemView.setOnClickListener {
                clickListener?.onGrupoClick(grupo)
            }
        }
    }
}

class GrupoDiffCallback : DiffUtil.ItemCallback<Grupo>() {
    override fun areItemsTheSame(oldItem: Grupo, newItem: Grupo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Grupo, newItem: Grupo): Boolean {
        return oldItem == newItem
    }
}