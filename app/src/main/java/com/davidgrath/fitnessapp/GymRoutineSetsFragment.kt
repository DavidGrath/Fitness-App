package com.davidgrath.fitnessapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.davidgrath.fitnessapp.data.entities.GymRoutineTemplate
import com.davidgrath.fitnessapp.databinding.FragmentGymWorkoutSetsBinding
import com.davidgrath.fitnessapp.framework.FitnessApp
import com.davidgrath.fitnessapp.util.SimpleResult

class GymRoutineSetsFragment : Fragment(), OnClickListener {

    interface GymRoutineSetsListener {
        fun onStartRoutine(routineTemplatesIndex: Int)
    }

    private var listener: GymRoutineSetsListener? = null
    private lateinit var viewModel: GymTestViewModel
    private lateinit var binding: FragmentGymWorkoutSetsBinding
    private lateinit var routineTemplate: GymRoutineTemplate
    private var routineTemplateIndex: Int = 0
    private lateinit var adapter: GymSetsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is GymRoutineSetsListener) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentGymWorkoutSetsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(GymTestViewModel::class.java)

        val index = requireArguments().getInt(BUNDLE_ARG_ROUTINE_TEMPLATES_INDEX)
        routineTemplate = (requireActivity().application as FitnessApp).defaultGymRoutineTemplates[index]
        routineTemplateIndex = index

        binding.textViewGymWorkoutSetsTitle.text = routineTemplate.routineName

        val setIdentifierTitles = (requireActivity().application as FitnessApp).setIdentifierTitles
        adapter = GymSetsAdapter(routineTemplate.sets, setIdentifierTitles)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewGymTestSets.adapter = adapter
        binding.recyclerViewGymTestSets.layoutManager = layoutManager

        binding.buttonGymWorkoutSetsStartWorkout.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View?) {
        v?.let {
            when(it) {
                binding.buttonGymWorkoutSetsStartWorkout -> {
                    viewModel.addWorkout(routineTemplate.routineName).observe(viewLifecycleOwner) {
                        when(it) {
                            is SimpleResult.Processing -> {
                                binding.buttonGymWorkoutSetsStartWorkout.isEnabled = false
                            }
                            is SimpleResult.Success -> {
                                listener?.onStartRoutine(routineTemplateIndex)
                            }
                            is SimpleResult.Failure -> {

                            }
                        }
                    }
                }
            }
        }
    }

    companion object {

        private const val BUNDLE_ARG_ROUTINE_TEMPLATES_INDEX = "routineTemplatesIndex"

        fun newInstance(routineTemplatesIndex: Int): GymRoutineSetsFragment {
            val fragment = GymRoutineSetsFragment()
            val args = Bundle().apply {
                putInt(BUNDLE_ARG_ROUTINE_TEMPLATES_INDEX, routineTemplatesIndex)
            }
            fragment.arguments = args
            return fragment
        }
    }
}

