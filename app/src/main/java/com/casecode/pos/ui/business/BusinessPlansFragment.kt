package com.casecode.pos.ui.business

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.casecode.pos.databinding.FragmentBusinessPlansBinding
import com.casecode.pos.ui.stepper.StepperActivity

class BusinessPlansFragment : Fragment() {

    private var binding : FragmentBusinessPlansBinding? = null
    private  val _binding: FragmentBusinessPlansBinding get() =  binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentBusinessPlansBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.btnBusinessPlansDone.setOnClickListener {
            (activity as StepperActivity).getNextStep()

        }

        _binding.btnBusinessPlansBranches.setOnClickListener {
            (activity as StepperActivity).getPreviousStep()

        }
    }

}