package com.casecode.pos.ui.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.casecode.domain.model.users.Item
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.adapter.ItemAdapter
import com.casecode.pos.databinding.FragmentItemsBinding
import com.casecode.pos.utils.showToast
import com.casecode.pos.viewmodel.ItemsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for displaying a list of items.
 */
@AndroidEntryPoint
class ItemsFragment : Fragment() {

    // View binding instance
    private lateinit var binding: FragmentItemsBinding

    // ViewModel instance
    private val viewModel: ItemsViewModel by activityViewModels()

    // Adapter for the item list RecyclerView
    private val itemAdapter = ItemAdapter(
        itemClick = { item -> onItemClicked(item) },
        itemLongClick = { item -> onDeleteItem(item) }
    )

    /**
     * Inflates the fragment layout and sets up the RecyclerView.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemsBinding.inflate(inflater, container, false)

        // Set up RecyclerView
        binding.recyclerItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemAdapter
        }

        return binding.root
    }

    /**
     * Initializes UI elements and observes LiveData.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listener for add item floating action button
        binding.floatingAddItem.setOnClickListener { showItemDialog() }

        // Observe items LiveData
        observeItems()
    }

    /**
     * Observes LiveData for item list and item action state.
     */
    private fun observeItems() {
        // Observe items LiveData
        viewModel.items.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Empty -> showMessage("Items is empty data!.")
                is Resource.Error -> showMessage(result.message.toString())
                is Resource.Loading -> showLoading()
                is Resource.Success -> {
                    itemAdapter.submitList(result.data.toMutableList())
                    hideLoading()
                }
            }
        }

        // Observe itemActionState LiveData
        viewModel.itemActionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Empty -> showMessage("Item action state is empty data!.")
                is Resource.Error -> showMessage(state.message.toString())
                is Resource.Loading -> showLoading()
                is Resource.Success -> showMessage(state.data)
            }
        }
    }

    /**
     * Displays the item dialog for adding or updating an item.
     *
     * @param isUpdate Flag indicating whether it's an update operation.
     * @param item The item to be updated (if updating).
     */
    private fun showItemDialog(isUpdate: Boolean = false, item: Item? = null) {
        val newInstance = ItemDialogFragment.newInstance(isUpdate, item)
        newInstance.show(childFragmentManager, ItemDialogFragment.ITEM_DIALOG_FRAGMENT)
    }

    /**
     * Handles item click event by showing the item dialog for update.
     *
     * @param item The clicked item.
     */
    private fun onItemClicked(item: Item) {
        showItemDialog(isUpdate = true, item = item)
    }

    /**
     * Handles item long click event by prompting the user to delete the item.
     *
     * @param item The long-clicked item.
     */
    private fun onDeleteItem(item: Item) {
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_item_title))
            .setMessage(getString(R.string.delete_item_message))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                // If there is no link to the image, just delete the item
                if (item.imageUrl == null) {
                    viewModel.deleteItem(item)
                } else {
                    viewModel.deleteImageAndDeleteItem(item)
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }

    /**
     * Displays a message to the user.
     *
     * @param message The message to be displayed.
     */
    private fun showMessage(message: String) {
        binding.root.showToast(message, Toast.LENGTH_SHORT)
        hideLoading()
    }

    /**
     * Displays the loading indicator.
     */
    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    /**
     * Hides the loading indicator.
     */
    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }
}