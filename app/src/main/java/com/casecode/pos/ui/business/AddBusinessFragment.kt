package com.casecode.pos.ui.business

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.casecode.pos.databinding.FragmentAddBusinessBinding
import com.casecode.pos.ui.stepper.StepperActivity


class AddBusinessFragment : Fragment() {
    private var _binding : FragmentAddBusinessBinding? = null
    private  val binding: FragmentAddBusinessBinding get() =  _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentAddBusinessBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAdBusinessBranches.setOnClickListener{
            (activity as StepperActivity).getNextStep()

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding  = null
    }


}