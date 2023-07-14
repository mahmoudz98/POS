package com.casecode.pos.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.casecode.pos.databinding.FragmentProdcutsBinding
import com.casecode.pos.viewmodel.ProudctsViewModel

class ProductsFragment : Fragment() {


    private lateinit var viewModel: ProudctsViewModel
    private var _binding: FragmentProdcutsBinding? = null
    private val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProdcutsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ProudctsViewModel::class.java]

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}