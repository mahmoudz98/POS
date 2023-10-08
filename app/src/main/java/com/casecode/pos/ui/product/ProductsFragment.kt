package com.casecode.pos.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.casecode.pos.databinding.FragmentProdcutsBinding
import com.casecode.pos.viewmodel.ProudctsViewModel

class ProductsFragment : Fragment() {

    private lateinit var binding: FragmentProdcutsBinding

    private val viewModel: ProudctsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProdcutsBinding.inflate(inflater, container, false)

        return binding.root
    }

}