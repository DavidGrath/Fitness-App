package com.davidgrath.fitnessapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.davidgrath.fitnessapp.data.entities.SimpleIntlString
import com.davidgrath.fitnessapp.data.entities.YogaSessionTemplate
import com.davidgrath.fitnessapp.databinding.RecyclerViewGymSetItemBinding
import com.davidgrath.fitnessapp.databinding.RecyclerViewYogaAsanaItemBinding
import com.davidgrath.fitnessapp.util.setIdentifierToIconMap

class YogaAsanasAdapter(
    private val asanas: List<YogaSessionTemplate.YogaAsanaTemplate>,
    private val asanaTitleMap: Map<String, SimpleIntlString>
): RecyclerView.Adapter<YogaAsanasAdapter.YogaAsanasViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YogaAsanasViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerViewYogaAsanaItemBinding.inflate(inflater, parent, false)
        return YogaAsanasViewHolder(binding)
    }

    override fun onBindViewHolder(holder: YogaAsanasViewHolder, position: Int) {
        val asana = asanas[position]
        with(holder.binding) {
//            imageViewYogaAsanaItemIcon.setImageResource() //TODO Find thumbnails to use
            textViewYogaAsanaItemTitle.text = (asanaTitleMap[asana.identifier])?._default
        }
    }

    override fun getItemCount(): Int {
        return asanas.size
    }

    class YogaAsanasViewHolder(val binding: RecyclerViewYogaAsanaItemBinding): ViewHolder(binding.root)
}