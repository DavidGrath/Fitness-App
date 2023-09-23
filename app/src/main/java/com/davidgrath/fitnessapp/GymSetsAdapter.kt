package com.davidgrath.fitnessapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.davidgrath.fitnessapp.data.entities.GymRoutineTemplate
import com.davidgrath.fitnessapp.data.entities.SimpleIntlString
import com.davidgrath.fitnessapp.databinding.RecyclerViewGymSetItemBinding
import com.davidgrath.fitnessapp.util.setIdentifierToIconMap

class GymSetsAdapter(
    private val sets: List<GymRoutineTemplate.GymSetTemplate>,
    private val setTitleMap: Map<String, SimpleIntlString>
    ): RecyclerView.Adapter<GymSetsAdapter.GymSetsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymSetsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerViewGymSetItemBinding.inflate(inflater, parent, false)
        return GymSetsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GymSetsViewHolder, position: Int) {
        val set = sets[position]
        with(holder.binding) {
            val iconId = setIdentifierToIconMap[set.identifier]
            if(iconId != null) {
                imageViewGymSetItemIcon.setImageResource(iconId)
            }
            textViewGymSetItemTitle.text = (setTitleMap[set.identifier])?._default
        }
    }

    override fun getItemCount(): Int {
        return sets.size
    }

    class GymSetsViewHolder(val binding: RecyclerViewGymSetItemBinding): RecyclerView.ViewHolder(binding.root)
}

