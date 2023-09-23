package com.davidgrath.fitnessapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.davidgrath.fitnessapp.data.entities.YogaSessionTemplate
import com.davidgrath.fitnessapp.databinding.FragmentYogaSessionAsanasBinding
import com.davidgrath.fitnessapp.framework.FitnessApp
import com.davidgrath.fitnessapp.util.SimpleResult

class YogaSessionAsanasFragment : Fragment(), View.OnClickListener {

    interface YogaSessionAsanasListener {
        fun onStartSession(sessionTemplatesIndex: Int)
    }

    private var listener: YogaSessionAsanasListener? = null
    private lateinit var viewModel: YogaTestViewModel
    private lateinit var binding: FragmentYogaSessionAsanasBinding
    private lateinit var yogaSessionTemplate: YogaSessionTemplate
    private var sessionTemplateIndex: Int = 0
    private lateinit var adapter: YogaAsanasAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is YogaSessionAsanasListener) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentYogaSessionAsanasBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(YogaTestViewModel::class.java)

        val index = requireArguments().getInt(BUNDLE_ARG_SESSION_TEMPLATES_INDEX)
        yogaSessionTemplate = (requireActivity().application as FitnessApp).defaultYogaSessionTemplates[index]
        sessionTemplateIndex = index

        binding.textViewYogaSessionAsanasTitle.text = yogaSessionTemplate.sessionName
        binding.textViewYogaSessionAsanasDescription.text = yogaSessionTemplate.sessionDescription

        val asanaIdentifierTitles = (requireActivity().application as FitnessApp).asanaIdentifierTitles
        adapter = YogaAsanasAdapter(yogaSessionTemplate.asanas, asanaIdentifierTitles)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewYogaTestAsanas.adapter = adapter
        binding.recyclerViewYogaTestAsanas.layoutManager = layoutManager

        binding.buttonYogaSessionAsanasStartWorkout.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View?) {
        v?.let {
            when(it) {
                binding.buttonYogaSessionAsanasStartWorkout -> {
                    viewModel.addWorkout(yogaSessionTemplate.sessionName).observe(viewLifecycleOwner) {
                        when(it) {
                            is SimpleResult.Processing -> {
                                binding.buttonYogaSessionAsanasStartWorkout.isEnabled = false
                            }
                            is SimpleResult.Success -> {
                                listener?.onStartSession(sessionTemplateIndex)
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

        private const val BUNDLE_ARG_SESSION_TEMPLATES_INDEX = "sessionTemplatesIndex"

        fun newInstance(sessionTemplatesIndex: Int): YogaSessionAsanasFragment {
            val fragment = YogaSessionAsanasFragment()
            val args = Bundle().apply {
                putInt(BUNDLE_ARG_SESSION_TEMPLATES_INDEX, sessionTemplatesIndex)
            }
            fragment.arguments = args
            return fragment
        }
    }
}