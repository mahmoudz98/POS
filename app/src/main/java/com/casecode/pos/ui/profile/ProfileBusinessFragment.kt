package com.casecode.pos.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.casecode.pos.databinding.FragmentProfileBusinessBinding
import com.casecode.pos.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileBusinessFragment : Fragment() {

    private lateinit var binding: FragmentProfileBusinessBinding
    private val viewModel: ProfileViewModel by viewModels(
    ownerProducer = { requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBusinessBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setup()
    }

    private fun setup() {
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.business.observe(viewLifecycleOwner){
            binding.business
        }
    }

}