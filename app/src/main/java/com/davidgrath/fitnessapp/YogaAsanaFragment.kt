package com.davidgrath.fitnessapp

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.davidgrath.fitnessapp.data.entities.YogaAsanaState
import com.davidgrath.fitnessapp.data.entities.YogaSessionTemplate
import com.davidgrath.fitnessapp.databinding.FragmentYogaAsanaBinding
import com.davidgrath.fitnessapp.framework.FitnessApp
import com.davidgrath.fitnessapp.util.SimpleResult
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class YogaAsanaFragment: Fragment(), View.OnClickListener {

    interface YogaAsanaListener {
        fun onNextAsana(sessionTemplatesIndex: Int, currentTemplateAsanaIndex: Int)
        fun onSessionFinished()
    }

    private lateinit var sessionTemplate: YogaSessionTemplate
    private var sessionTemplateIndex: Int = 0
    private lateinit var asanaTemplate: YogaSessionTemplate.YogaAsanaTemplate
    private var asanaTemplateIndex: Int = 0
    private lateinit var binding: FragmentYogaAsanaBinding
    private lateinit var viewModel: YogaTestViewModel
    private var listener: YogaAsanaListener? = null
    private var yogaAsanaStateCached = YogaAsanaState(0, false)
    private var pauseDrawable: Drawable? = null
    private var playDrawable: Drawable? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is YogaAsanaListener) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentYogaAsanaBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(YogaTestViewModel::class.java)

        val sessionIndex = requireArguments().getInt(BUNDLE_ARG_SESSION_TEMPLATES_INDEX)
        sessionTemplate = (requireActivity().application as FitnessApp).defaultYogaSessionTemplates[sessionIndex]
        sessionTemplateIndex = sessionIndex
        val asanaIndex = requireArguments().getInt(BUNDLE_ARG_TEMPLATE_ASANA_INDEX)
        asanaTemplate = sessionTemplate.asanas[asanaIndex]
        asanaTemplateIndex = asanaIndex

        // TODO Get thumbnails
//        binding.imageViewGymSetIcon.setImageResource(iconId)

        val asanaIdentifierTitles = (requireActivity().application as FitnessApp).asanaIdentifierTitles
        binding.textViewYogaAsanaTitle.text = (asanaIdentifierTitles[asanaTemplate.identifier])?._default

        if(savedInstanceState == null) {
            viewModel.startAsana(asanaTemplate.identifier, asanaTemplate.durationMillis)
        }
        //TODO Timer
//        val counterText = String.format("%02d", currentRepCount)
//        binding.textViewGymSetCounter.text = counterText

        if(asanaTemplateIndex + 1 >= sessionTemplate.asanas.size) {
            binding.buttonYogaAsanaNext.setCompoundDrawables(null, null, null, null)
            binding.buttonYogaAsanaNext.text = "Finish"
        } else {

        }

        pauseDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_pause_24_white)
        playDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_play_arrow_24_white)
        viewModel.getYogaAsanaState().observe(viewLifecycleOwner) { yogaAsanaState ->
            yogaAsanaStateCached = yogaAsanaState
            val duration = yogaAsanaState.timeLeft.toDuration(DurationUnit.MILLISECONDS)
            duration.toComponents { hours, minutes, seconds, nanoseconds ->
                val roundedSeconds = if(nanoseconds / 1_000_000 > 0) {
                    seconds + 1
                } else {
                    seconds
                }
                val formatted = if(hours > 0) {
                    String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", roundedSeconds)
                } else {
                    String.format("%02d", minutes) + ":" + String.format("%02d", roundedSeconds)
                }
                binding.textViewYogaAsanaCounter.text = formatted
            }
            if(yogaAsanaState.timeLeft > 0) {
                if(yogaAsanaState.isPaused) {
                    binding.buttonYogaAsanaNext.setCompoundDrawablesRelativeWithIntrinsicBounds(playDrawable, null, null, null)
                    binding.buttonYogaAsanaNext.text = "Resume"
                } else {
                    binding.buttonYogaAsanaNext.setCompoundDrawablesRelativeWithIntrinsicBounds(pauseDrawable, null, null, null)
                    binding.buttonYogaAsanaNext.text = "Pause"
                }
            } else {
                if(asanaTemplateIndex + 1 >= sessionTemplate.asanas.size) {
                    binding.buttonYogaAsanaNext.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
                    binding.buttonYogaAsanaNext.text = "Finish"
                } else {
                    binding.buttonYogaAsanaNext.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
                    binding.buttonYogaAsanaNext.text = "Next"
                }
            }
        }

        binding.buttonYogaAsanaNext.setOnClickListener(this)
        binding.linearLayoutYogaAsanaSkip.setOnClickListener(this)
        binding.linearLayoutYogaAsanaPrevious.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(v: View?) {
        v?.let {
            when(it) {
                binding.buttonYogaAsanaNext -> {
                    if(yogaAsanaStateCached.timeLeft > 0) {
                        if(yogaAsanaStateCached.isPaused) {
                            viewModel.resumeAsana()
                        } else {
                            viewModel.pauseAsana()
                        }
                    } else {
                        if (asanaTemplateIndex + 1 >= sessionTemplate.asanas.size) {
                            viewModel.endCurrentWorkout().observe(viewLifecycleOwner) { result ->
                                when (result) {
                                    is SimpleResult.Processing -> {

                                    }

                                    is SimpleResult.Success -> {
                                        listener?.onSessionFinished()
                                    }

                                    is SimpleResult.Failure -> {

                                    }
                                }
                            }

                        } else {
                            viewModel.endAsana().observe(viewLifecycleOwner) { result ->
                                when (result) {
                                    is SimpleResult.Processing -> {

                                    }

                                    is SimpleResult.Success -> {
                                        listener?.onNextAsana(
                                            sessionTemplateIndex,
                                            asanaTemplateIndex
                                        )
                                    }

                                    is SimpleResult.Failure -> {

                                    }
                                }
                            }
                        }
                    }
                }
                binding.linearLayoutYogaAsanaSkip -> {
//                    if (asanaTemplateIndex + 1 >= sessionTemplate.asanas.size) {
//                        viewModel.endAsana().observe(viewLifecycleOwner) { }
//                    } else {
                        viewModel.skipAsana().observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is SimpleResult.Processing -> {

                                }

                                is SimpleResult.Success -> {
                                    if (asanaTemplateIndex + 1 >= sessionTemplate.asanas.size) {

                                    } else {
                                        listener?.onNextAsana(
                                            sessionTemplateIndex,
                                            asanaTemplateIndex
                                        )
                                    }
                                }

                                is SimpleResult.Failure -> {

                                }
                            }
                        }
//                    }
                }
                binding.linearLayoutYogaAsanaPrevious -> {

                }
                else -> {

                }
            }
        }
    }

    companion object {

        private const val BUNDLE_ARG_SESSION_TEMPLATES_INDEX = "sessionTemplatesIndex"
        private const val BUNDLE_ARG_TEMPLATE_ASANA_INDEX = "templateAsanaIndex"

        @JvmStatic
        fun newInstance(sessionTemplatesIndex: Int, templateAsanaIndex: Int): YogaAsanaFragment {
            val fragment = YogaAsanaFragment()
            val args = Bundle().apply {
                putInt(BUNDLE_ARG_SESSION_TEMPLATES_INDEX, sessionTemplatesIndex)
                putInt(BUNDLE_ARG_TEMPLATE_ASANA_INDEX, templateAsanaIndex)
            }
            fragment.arguments = args
            return fragment
        }
    }
}