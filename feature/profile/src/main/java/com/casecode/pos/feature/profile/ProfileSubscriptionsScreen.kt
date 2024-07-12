package com.casecode.pos.feature.profile/*
package com.casecode.pos.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.casecode.pos.adapter.SubscriptionAdapter
import com.casecode.pos.databinding.FragmentProfileSubscriptionsBinding
import com.casecode.pos.ui.profile.ProfileViewModel


class ProfileSubscriptionsFragment : Fragment() {

    private lateinit var binding: FragmentProfileSubscriptionsBinding
    private val viewModel: ProfileViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileSubscriptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner
        setup()
    }

    private fun setup() {
        setupAdapter()
        viewModel.getSubscriptionsBusiness()
        setupObservers()
    }

    private fun setupAdapter() {
        val subscriptionAdapter: SubscriptionAdapter by lazy {
            SubscriptionAdapter( {
               // viewModel.addSubscriptionBusinessSelected(it)
            })
        }
        binding.rvBusinessSubscription.adapter = subscriptionAdapter
    }

    private fun setupObservers() {
        // TODO: handle add new subscription with business and handle old subscriptions
        viewModel.isSubscriptionsError.observe(viewLifecycleOwner) {
            binding.isErrorSubscriptions = it
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.isLoading = it
        }
        viewModel.subscriptions.observe(viewLifecycleOwner) {
            binding.subscriptions = it
        }
    }

}*/