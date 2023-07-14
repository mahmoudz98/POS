package com.casecode.pos.ui.pos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.casecode.pos.databinding.FragmentPointOfSaleBinding
import com.casecode.pos.viewmodel.PointOfSaleViewModel

class PointOfSaleFragment : Fragment() {

    private var _binding: FragmentPointOfSaleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val pointOfSaleViewModel = ViewModelProvider(this)[PointOfSaleViewModel::class.java]

        _binding = FragmentPointOfSaleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        pointOfSaleViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}