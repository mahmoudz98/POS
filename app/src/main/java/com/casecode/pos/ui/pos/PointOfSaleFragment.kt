package com.casecode.pos.ui.pos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.casecode.pos.databinding.FragmentPointOfSaleBinding
import com.casecode.pos.viewmodel.PointOfSaleViewModel

class PointOfSaleFragment : Fragment() {

    private lateinit var binding: FragmentPointOfSaleBinding
    private val viewModel: PointOfSaleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPointOfSaleBinding.inflate(inflater, container, false)

        val textView: TextView = binding.textGallery
        viewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return binding.root
    }

}