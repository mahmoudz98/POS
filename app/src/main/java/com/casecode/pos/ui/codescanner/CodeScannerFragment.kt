package com.casecode.pos.ui.codescanner

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.casecode.pos.databinding.FragmentCodeScannerBinding
import com.casecode.pos.viewmodel.CodeScannerViewModel

class CodeScannerFragment : Fragment() {



    private lateinit var viewModel: CodeScannerViewModel
    private var binding: FragmentCodeScannerBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentCodeScannerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CodeScannerViewModel::class.java)

    }



}