package com.casecode.pos.ui.invoices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.casecode.pos.databinding.FragmentInvoicesBinding
import com.casecode.pos.viewmodel.InvoicesViewModel

class InvoicesFragment : Fragment() {

    private lateinit var binding: FragmentInvoicesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val invoicesViewModel =
            ViewModelProvider(this).get(InvoicesViewModel::class.java)

        binding = FragmentInvoicesBinding.inflate(inflater, container, false)

        val textView: TextView = binding.textSlideshow
        invoicesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return binding.root
    }

}