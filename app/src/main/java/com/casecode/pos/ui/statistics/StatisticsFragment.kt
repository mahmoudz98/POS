package com.casecode.pos.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.casecode.domain.entity.BasicItem
import com.casecode.domain.entity.Store
import com.casecode.pos.databinding.FragmentStatisticsBinding
import com.casecode.pos.utils.FirebaseResult
import com.casecode.pos.viewmodel.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding

    private val viewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)


        viewModel.getDocuments("stores").observe(viewLifecycleOwner) { storesDocuments ->
            when (storesDocuments) {
                is FirebaseResult.Success -> {
                    storesDocuments.data.map { document ->
                        val documentId = document.id
                        Timber.tag("test").i("documentId: $documentId")

                        val store: Store? = document.toObject(Store::class.java)
                        Timber.tag("test").i("store: $store")

                        viewModel.getDocuments("stores", documentId, "basicItems")
                            .observe(viewLifecycleOwner) { basicItemsDocuments ->
                                when (basicItemsDocuments) {
                                    is FirebaseResult.Success -> {
                                        basicItemsDocuments.data.map { basicItemsDocument ->
                                            val basicItem: BasicItem? =
                                                basicItemsDocument.toObject(BasicItem::class.java)
                                            Timber.tag("test").i("basicItem: $basicItem")
                                        }
                                    }

                                    is FirebaseResult.Failure -> {
                                        // Handle failure case
                                    }
                                }
                            }
                    }
                }

                is FirebaseResult.Failure -> {
                    // Handle failure case
                }
            }
        }

        return binding.root
    }

}