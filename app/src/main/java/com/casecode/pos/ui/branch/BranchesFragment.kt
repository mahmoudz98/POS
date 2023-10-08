package com.casecode.pos.ui.branch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.casecode.pos.databinding.FragmentBranchesBinding
import com.casecode.pos.ui.stepper.StepperActivity


class BranchesFragment : Fragment() {

    private lateinit var binding: FragmentBranchesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBranchesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnBranchesPlan.setOnClickListener { (activity as StepperActivity).getNextStep() }

            btnBranchesInfo.setOnClickListener { (activity as StepperActivity).getPreviousStep() }

            branches.btnBranchesAdd.setOnClickListener {
                val dialog = AddBranchesDialogFragment()
                dialog.show(parentFragmentManager, "Dialog")
            }
        }
    }

}