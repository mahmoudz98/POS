package com.casecode.pos.ui.codescanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.casecode.pos.databinding.FragmentCodeScannerBinding
import com.casecode.pos.viewmodel.CodeScannerViewModel

class CodeScannerFragment : Fragment() {

    private lateinit var binding: FragmentCodeScannerBinding

    private val viewModel: CodeScannerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCodeScannerBinding.inflate(inflater, container, false)

        return binding.root
    }

}