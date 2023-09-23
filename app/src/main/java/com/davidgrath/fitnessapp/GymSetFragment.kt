package com.davidgrath.fitnessapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.davidgrath.fitnessapp.data.entities.GymRoutineTemplate
import com.davidgrath.fitnessapp.databinding.FragmentGymSetBinding
import com.davidgrath.fitnessapp.framework.FitnessApp
import com.davidgrath.fitnessapp.util.SimpleResult
import com.davidgrath.fitnessapp.util.setIdentifierToIconMap

class GymSetFragment: Fragment(), OnClickListener {
    interface GymSetListener {
        fun onNextSet(routineTemplatesIndex: Int, currentTemplateSetIndex: Int)
        fun onRoutineFinished()
    }

    private lateinit var routineTemplate: GymRoutineTemplate
    private var routineTemplateIndex: Int = 0
    private lateinit var setTemplate: GymRoutineTemplate.GymSetTemplate
    private var setTemplateIndex: Int = 0
    private lateinit var binding: FragmentGymSetBinding
    private lateinit var viewModel: GymTestViewModel
    //TODO Move to ViewModel for sake of timer
    private var currentRepCount = 0
    private var listener: GymSetListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is GymSetListener) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentGymSetBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(GymTestViewModel::class.java)

        val routineIndex = requireArguments().getInt(BUNDLE_ARG_ROUTINE_TEMPLATES_INDEX)
        routineTemplate = (requireActivity().application as FitnessApp).defaultGymRoutineTemplates[routineIndex]
        routineTemplateIndex = routineIndex
        val setIndex = requireArguments().getInt(BUNDLE_ARG_TEMPLATE_SET_INDEX)
        setTemplate = routineTemplate.sets[setIndex]
        setTemplateIndex = setIndex

        val iconId = setIdentifierToIconMap[setTemplate.identifier]
        if(iconId != null) {
            binding.imageViewGymSetIcon.setImageResource(iconId)
        }
        val setIdentifierTitles = (requireActivity().application as FitnessApp).setIdentifierTitles
        binding.textViewGymSetTitle.text = (setIdentifierTitles[setTemplate.identifier])?._default

        if(savedInstanceState != null) {
            currentRepCount = savedInstanceState.getInt(TEMP_BUNDLE_ARG_CURRENT_REP_COUNT)
        } else {
            viewModel.startSet(setTemplate.identifier)
        }
        val counterText = String.format("%02d", currentRepCount)
        binding.textViewGymSetCounter.text = counterText
        if(setTemplateIndex + 1 >= routineTemplate.sets.size) {
            binding.buttonGymSetNext.setCompoundDrawables(null, null, null, null)
            binding.buttonGymSetNext.text = "Finish"
        } else {

        }

        binding.buttonGymSetNext.setOnClickListener(this)
        binding.imageViewGymSetCounterAdd.setOnClickListener(this)
        binding.imageViewGymSetCounterSubtract.setOnClickListener(this)
        binding.linearLayoutGymSetSkip.setOnClickListener(this)
        binding.linearLayoutGymSetPrevious.setOnClickListener(this)

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(TEMP_BUNDLE_ARG_CURRENT_REP_COUNT, currentRepCount)
    }

    override fun onClick(v: View?) {
        v?.let {
            when(it) {
                binding.buttonGymSetNext -> {
                    if (setTemplateIndex + 1 >= routineTemplate.sets.size) {
                        viewModel.endCurrentWorkout(currentRepCount)
                            .observe(viewLifecycleOwner) { result ->
                                when (result) {
                                    is SimpleResult.Processing -> {

                                    }
                                    is SimpleResult.Success -> {
                                        listener?.onRoutineFinished()
                                    }

                                    is SimpleResult.Failure -> {

                                    }
                                }
                            }

                    } else {
                        viewModel.endSet(currentRepCount)
                            .observe(viewLifecycleOwner) { result ->
                                when (result) {
                                    is SimpleResult.Processing -> {

                                    }

                                    is SimpleResult.Success -> {
                                        listener?.onNextSet(routineTemplateIndex, setTemplateIndex)
                                    }

                                    is SimpleResult.Failure -> {

                                    }
                                }
                            }
                    }
                }
                binding.imageViewGymSetCounterAdd -> {
                    if(currentRepCount + 1 <= 99) {
                        currentRepCount++
                    }
                    val counterText = String.format("%02d", currentRepCount)
                    binding.textViewGymSetCounter.text = counterText
                }
                binding.imageViewGymSetCounterSubtract -> {
                    if(currentRepCount - 1 >= 0) {
                        currentRepCount--
                    }
                    val counterText = String.format("%02d", currentRepCount)
                    binding.textViewGymSetCounter.text = counterText
                }
                binding.linearLayoutGymSetSkip -> {
                    viewModel.skipSet()
                    listener?.onNextSet(routineTemplateIndex, setTemplateIndex)
                }
                binding.linearLayoutGymSetPrevious -> {

                }
                else -> {

                }
            }
        }
    }

    companion object {

        private const val BUNDLE_ARG_ROUTINE_TEMPLATES_INDEX = "routineTemplatesIndex"
        private const val BUNDLE_ARG_TEMPLATE_SET_INDEX = "templateSetIndex"
        private const val TEMP_BUNDLE_ARG_CURRENT_REP_COUNT = "currentRepCount"

        @JvmStatic
        fun newInstance(routineTemplatesIndex: Int, templateSetIndex: Int): GymSetFragment {
            val fragment = GymSetFragment()
            val args = Bundle().apply {
                putInt(BUNDLE_ARG_ROUTINE_TEMPLATES_INDEX, routineTemplatesIndex)
                putInt(BUNDLE_ARG_TEMPLATE_SET_INDEX, templateSetIndex)
            }
            fragment.arguments = args
            return fragment
        }
    }
}