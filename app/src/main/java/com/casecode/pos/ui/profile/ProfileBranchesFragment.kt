package com.casecode.pos.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.casecode.pos.databinding.FragmentProfileBranchesBinding
import com.casecode.pos.ui.branch.AddBranchesDialogFragment


class ProfileBranchesFragment : Fragment() {

    private lateinit var binding: FragmentProfileBranchesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBranchesBinding.inflate(inflater, container, false)

        binding.profileBranches.btnBranchesAdd.setOnClickListener {
            val dialog = AddBranchesDialogFragment()
            dialog.show(parentFragmentManager, "Dialog")
        }

        return binding.root
    }

}