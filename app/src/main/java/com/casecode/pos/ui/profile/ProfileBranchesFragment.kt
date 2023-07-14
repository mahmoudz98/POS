package com.casecode.pos.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.casecode.pos.R
import com.casecode.pos.databinding.FragmentProfileBranchesBinding
import com.casecode.pos.ui.branch.AddBranchesDialogFragment


class ProfileBranchesFragment : Fragment() {


    private var _binding : FragmentProfileBranchesBinding? = null
    private val binding: FragmentProfileBranchesBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentProfileBranchesBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileBranches.btnBranchesAdd.setOnClickListener{
            val dialog = AddBranchesDialogFragment()
            dialog.show(parentFragmentManager, "Dialog")
        }
    }


}