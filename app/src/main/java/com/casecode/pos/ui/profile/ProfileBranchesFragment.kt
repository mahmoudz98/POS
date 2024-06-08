/*
package com.casecode.pos.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.casecode.pos.R
import com.casecode.pos.adapter.BranchesAdapter
import com.casecode.pos.databinding.FragmentProfileBranchesBinding
import com.casecode.pos.ui.branch.AddBranchesDialogFragment
import com.casecode.pos.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileBranchesFragment : Fragment() {

    private lateinit var binding: FragmentProfileBranchesBinding
    private val viewModel: ProfileViewModel by viewModels(
        ownerProducer = { requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBranchesBinding.inflate(inflater, container, false)

        binding.lProfileBranches.btnBranchesAdd.setOnClickListener {
            val dialog = ProfileBranchDialog()
            dialog.show(childFragmentManager,ProfileBranchDialog.ADD_BRANCH_TAG )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner
        setup()
    }

    private fun setup() {
        setupObserver()
        setupAdapter()
    }

    private fun setupObserver() {
        viewModel.business.observe(viewLifecycleOwner){
            val branches = it.branches
            binding.lProfileBranches.branches = branches
        }
    }
    private fun setupAdapter() {
        // Lazy initialization of BranchesAdapter
        val branchAdapter: BranchesAdapter by lazy {
            BranchesAdapter {}
        }
        binding.lProfileBranches.rvBranches.adapter = branchAdapter
    }

}*/