/*
package com.casecode.pos.ui.business

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.casecode.pos.adapter.SubscriptionAdapter
import com.casecode.pos.databinding.FragmentBusinessSubscriptionBinding
import com.casecode.pos.viewmodel.StepperBusinessViewModel
import dagger.hilt.android.AndroidEntryPoint

*/
/**
 * A fragment that displays the business Subscription.
 *//*

@AndroidEntryPoint
class BusinessSubscriptionFragment : Fragment() {
    @Suppress("ktlint:standard:property-naming")
    private var _binding: FragmentBusinessSubscriptionBinding? = null
    private val binding get() = _binding!!
    internal val businessViewModel by activityViewModels<StepperBusinessViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBusinessSubscriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner
        setup()
    }

    private fun setup() {
        setupObservers()
        setupAdapter()
        setupClickListener()
    }

    private fun setupObservers() {
        observerNetworkAndGetSubscriptions()
        observerSubscriptions()
        observerDataSubscriptionsIsLoadingOrError()
    }

    private fun observerDataSubscriptionsIsLoadingOrError() {
        businessViewModel.isSubscriptionsError.observe(viewLifecycleOwner) {
            binding.isErrorSubscriptions = it
        }
        businessViewModel.isLoading.observe(viewLifecycleOwner) {
            binding.isLoading = it
        }
    }

    private fun observerSubscriptions() {
        businessViewModel.subscriptions.observe(viewLifecycleOwner) {
            binding.subscriptions = it
        }
    }
    private fun observerNetworkAndGetSubscriptions() {
        businessViewModel.isOnline.observe(viewLifecycleOwner) {
            if (it) {
                businessViewModel.getSubscriptionsBusiness()
            } else {
                businessViewModel.subscriptionError()
            }
        }
    }
    private fun setupAdapter() {
        val subscriptionAdapter: SubscriptionAdapter by lazy {
            SubscriptionAdapter(itemClick =  {
                businessViewModel.addSubscriptionBusinessSelected(it)
            })
        }
        binding.rvBusinessSubscription.adapter = subscriptionAdapter
    }

    private fun setupClickListener() {
        binding.btnBusinessSubscriptionEmployee.setOnClickListener {
            businessViewModel.checkNetworkThenSetSubscriptionBusinessSelected()
        }
        binding.btnBusinessSubscriptionBranches.setOnClickListener {
            businessViewModel.moveToPreviousStep()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}*/