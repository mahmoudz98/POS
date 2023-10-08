package com.casecode.pos.ui.business

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.casecode.domain.entity.BasicItem
import com.casecode.domain.entity.Store
import com.casecode.pos.databinding.FragmentAddBusinessBinding
import com.casecode.pos.ui.stepper.StepperActivity
import com.casecode.pos.utils.FirebaseResult
import com.casecode.pos.viewmodel.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AddBusinessFragment : Fragment() {

    private lateinit var binding: FragmentAddBusinessBinding

    private val viewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddBusinessBinding.inflate(inflater, container, false)

        viewModel.getDocuments("stores").observe(viewLifecycleOwner) { documents ->
            when (documents) {
                is FirebaseResult.Success -> {
                    documents.data.map { document ->
                        val store: Store? = document.toObject(Store::class.java)
                        if (store != null) {
                            val storeCode = store.storeCode
                            val storeType = store.storeType
                            Timber.i("storeCode: $storeCode")
                            Timber.i("storeType: $storeType")

                            val basicItems: List<BasicItem>? = store.basicItems
                            Timber.i("basicItems: $basicItems")

                            val basicItem: BasicItem? = document.toObject(BasicItem::class.java)
                            Timber.i("basicItem: $basicItem")

                            basicItems?.let { items ->
                                for (basicItem in items) {
                                    val name = basicItem.name
                                    val sku = basicItem.sku
                                    val unitOfMeasurement = basicItem.unitOfMeasurement
                                    val image = basicItem.image
                                    Timber.i("name: $name")
                                    Timber.i("sku: $sku")
                                    Timber.i("unitOfMeasurement: $unitOfMeasurement")
                                    Timber.i("image: $image")
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAdBusinessBranches.setOnClickListener {
            (activity as StepperActivity).getNextStep()
        }
    }

}