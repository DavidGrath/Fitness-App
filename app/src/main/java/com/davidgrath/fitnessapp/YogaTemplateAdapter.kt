package com.davidgrath.fitnessapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.davidgrath.fitnessapp.data.entities.YogaSessionTemplate
import com.davidgrath.fitnessapp.databinding.RecyclerViewYogaSessionTemplateItemBinding

class YogaTemplateAdapter(private var items: List<YogaSessionTemplate>, var listener: ItemClickListener? = null): RecyclerView.Adapter<YogaTemplateAdapter.YogaTemplateViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(position: Int, template: YogaSessionTemplate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YogaTemplateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerViewYogaSessionTemplateItemBinding.inflate(inflater, parent, false)
        return YogaTemplateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: YogaTemplateViewHolder, position: Int) {
        val template = items[position]
        with(holder.binding) {
            recyclerItemYogaSessionTemplateTitle.text = template.sessionName
            root.setOnClickListener {
                listener?.onItemClick(position, template)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class YogaTemplateViewHolder(val binding: RecyclerViewYogaSessionTemplateItemBinding): ViewHolder(binding.root)
}