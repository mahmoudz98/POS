package com.casecode.pos.ui.branch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.casecode.pos.databinding.FragmentBranchesBinding
import com.casecode.pos.ui.stepper.StepperActivity


class BranchesFragment : Fragment() {

    private var _binding: FragmentBranchesBinding? = null
    private val binding: FragmentBranchesBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBranchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBranchesPlan.setOnClickListener {
            (activity as StepperActivity).getNextStep()

        }
        binding.btnBranchesInfo.setOnClickListener {
            (activity as StepperActivity).getPreviousStep()

        }
        binding.branches.btnBranchesAdd.setOnClickListener{
            val dialog = AddBranchesDialogFragment()
            dialog.show(parentFragmentManager, "Dialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}