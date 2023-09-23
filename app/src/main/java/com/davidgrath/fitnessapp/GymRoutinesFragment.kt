package com.davidgrath.fitnessapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.davidgrath.fitnessapp.data.entities.GymRoutineTemplate
import com.davidgrath.fitnessapp.databinding.FragmentGymWorkoutTemplatesBinding
import com.davidgrath.fitnessapp.framework.FitnessApp
class GymRoutinesFragment : Fragment() {

    interface GymRoutinesListener {
        fun onRoutineClicked(position: Int, template: GymRoutineTemplate)
    }

    lateinit var binding: FragmentGymWorkoutTemplatesBinding
    lateinit var adapter: GymTemplateAdapter
    var listener: GymRoutinesListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is GymRoutinesListener) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGymWorkoutTemplatesBinding.inflate(inflater, container, false)
        adapter = GymTemplateAdapter((requireActivity().application as FitnessApp).defaultGymRoutineTemplates,  object: GymTemplateAdapter.ItemClickListener {
            override fun onItemClick(position: Int, template: GymRoutineTemplate) {
                listener?.onRoutineClicked(position, template)
            }
        })
        val layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerViewGymWorkoutTemplates.adapter = adapter
        binding.recyclerViewGymWorkoutTemplates.layoutManager = layoutManager
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() : GymRoutinesFragment {
            val fragment = GymRoutinesFragment()
            return fragment
        }
    }
}
