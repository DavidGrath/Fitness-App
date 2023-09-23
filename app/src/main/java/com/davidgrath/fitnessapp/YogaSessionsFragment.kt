package com.davidgrath.fitnessapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.davidgrath.fitnessapp.data.entities.YogaSessionTemplate
import com.davidgrath.fitnessapp.databinding.FragmentYogaWorkoutTemplatesBinding
import com.davidgrath.fitnessapp.framework.FitnessApp

class YogaSessionsFragment : Fragment() {

    interface YogaSessionsListener {
        fun onSessionClicked(position: Int, template: YogaSessionTemplate)
    }

    lateinit var binding: FragmentYogaWorkoutTemplatesBinding
    lateinit var adapter: YogaTemplateAdapter
    var listener: YogaSessionsListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is YogaSessionsListener) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentYogaWorkoutTemplatesBinding.inflate(inflater, container, false)
        adapter = YogaTemplateAdapter((requireActivity().application as FitnessApp).defaultYogaSessionTemplates,  object: YogaTemplateAdapter.ItemClickListener {
            override fun onItemClick(position: Int, template: YogaSessionTemplate) {
                listener?.onSessionClicked(position, template)
            }
        })
        val layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerViewYogaWorkoutTemplates.adapter = adapter
        binding.recyclerViewYogaWorkoutTemplates.layoutManager = layoutManager
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() : YogaSessionsFragment {
            val fragment = YogaSessionsFragment()
            return fragment
        }
    }

}