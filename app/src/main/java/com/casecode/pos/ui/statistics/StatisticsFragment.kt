package com.casecode.pos.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.casecode.domain.entity.Branches
import com.casecode.pos.R
import com.casecode.pos.databinding.FragmentStatisticsBinding
import com.casecode.pos.utils.FirebaseResult
import com.casecode.pos.viewmodel.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: StatisticsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.tvLastInvoice
        viewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        viewModel.getDocuments(getString(R.string.COLLECTION_BRANCHES))
            .observe(viewLifecycleOwner) { documents ->
                // Update UI with the documents
                when (documents) {
                    is FirebaseResult.Success -> {
                        documents.data.map { document ->
//                            val branches = document.toObject(Branches::class.java)

                            val branches: Branches? = document.toObject(Branches::class.java)

                            if (branches != null) {
                                Timber.i(document.id)
                            }
                        }
                    }

                    is FirebaseResult.Failure -> {
                        val exception = documents.exception
                        Timber.e(exception.message)
                    }
                }
            }

        val branch = Branches(1, "Case Code", "01022001263")

//        viewModel.setDocuments(
//            getString(R.string.COLLECTION_BRANCHES),
//            branch
//        ).observe(viewLifecycleOwner) { documentReference ->
//            when (documentReference) {
//                is FirebaseResult.Success -> {
//                    // Document was successfully added
//                    Timber.d("Document added with ID: ${documentReference.data.id}")
//                }
//
//                is FirebaseResult.Failure -> {
//                    // Error occurred while adding the document
//                    Timber.e("Error adding document",documentReference.exception)
//                }
//            }
//        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}