package com.davidgrath.fitnessapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.davidgrath.fitnessapp.data.entities.GymRoutineTemplate
import com.davidgrath.fitnessapp.databinding.RecyclerViewGymRoutineTemplateItemBinding

class GymTemplateAdapter(private var items: List<GymRoutineTemplate>, var listener: ItemClickListener? = null): RecyclerView.Adapter<GymTemplateAdapter.GymTemplateViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(position: Int, template: GymRoutineTemplate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymTemplateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerViewGymRoutineTemplateItemBinding.inflate(inflater, parent, false)
        return GymTemplateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GymTemplateViewHolder, position: Int) {
        val template = items[position]
        with(holder.binding) {
            recyclerItemGymRoutineTemplateTitle.text = template.routineName
            root.setOnClickListener {
                listener?.onItemClick(position, template)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class GymTemplateViewHolder(val binding: RecyclerViewGymRoutineTemplateItemBinding): ViewHolder(binding.root)
}

