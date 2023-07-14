package com.casecode.pos.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.casecode.pos.R
import com.casecode.pos.adapter.ProfilePagerAdapter
import com.casecode.pos.databinding.FragmentProfileBinding
import com.casecode.pos.viewmodel.ProfileViewModel
import com.google.android.material.tabs.TabLayoutMediator

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding : FragmentProfileBinding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)


        setupPagerAdapter()
        return binding.root
    }

    private fun setupPagerAdapter() {
        val profilePagerAdapter = ProfilePagerAdapter(this)
        binding.vpProfile.adapter = profilePagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.vpProfile) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.business_info_title)
                1 -> getString(R.string.branches)
                else -> getString(R.string.business_plans_title)
            }
        }.attach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}