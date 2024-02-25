package com.casecode.pos.ui.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.casecode.domain.model.users.Item
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.adapter.ItemInteractionAdapter
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

    private lateinit var binding: FragmentItemsBinding
    private val viewModel: ItemsViewModel by activityViewModels()
    private val itemInteractionAdapter = ItemInteractionAdapter(
        onItemClick = { item -> showItemDialog(isUpdate = true, item = item) },
        onItemLongClick = { item -> deleteItem(item = item) },
        onPrintButtonClick = { item -> showQRCodeDialog(item = item) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemsBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingAddItem.setOnClickListener { showItemDialog() }
        observeItems()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_items_fragment, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { itemInteractionAdapter.filterItems(it) }
                return true
            }
        })

        // Hide the action_main_profile menu item
        val profileMenuItem = menu.findItem(R.id.action_main_profile)
        profileMenuItem.isVisible = false
    }

    private fun setupRecyclerView() {
        binding.recyclerItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemInteractionAdapter
        }
    }

    private fun observeItems() {
        viewModel.items.observe(viewLifecycleOwner) { result ->
            handleItemsLiveData(result)
        }
        viewModel.itemActionState.observe(viewLifecycleOwner) { state ->
            handleItemActionState(state)
        }
    }

    private fun showItemDialog(isUpdate: Boolean = false, item: Item? = null) {
        val newInstance = ItemDialogFragment.newInstance(isUpdate, item)
        newInstance.show(childFragmentManager, ItemDialogFragment.ITEM_DIALOG_FRAGMENT)
    }

    private fun deleteItem(item: Item) {
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_item_title)
            .setMessage(R.string.delete_item_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                if (item.imageUrl == null) {
                    viewModel.deleteItem(item)
                } else {
                    viewModel.deleteImageAndDeleteItem(item)
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun showMessage(message: String) {
        with(binding) {
            root.showToast(message, Toast.LENGTH_SHORT)
            progressBar.visibility = View.GONE
        }
    }

    private fun showQRCodeDialog(item: Item) {
        val dialogFragment = QRCodeDialogFragment()
        val args = Bundle().apply {
            putString("barcode", item.sku)
            putString("name", item.name)
        }
        dialogFragment.arguments = args
        dialogFragment.show(childFragmentManager, "QRCodeDialog")
    }

    private fun handleItemsLiveData(result: Resource<List<Item>>) {
        when (result) {
            is Resource.Empty -> showEmptyView()
            is Resource.Error -> {
                hideAllViews()
                showMessage(result.message.toString())
            }

            is Resource.Loading -> showLoading()
            is Resource.Success -> {
                showRecyclerView()
                itemInteractionAdapter.submitList(result.data.toMutableList())
            }
        }
    }

    private fun handleItemActionState(state: Resource<Any>) {
        when (state) {
            is Resource.Empty -> showMessage(getString(R.string.item_action_state_empty))
            is Resource.Error -> showMessage(state.message.toString())
            is Resource.Loading -> showLoading()
            is Resource.Success -> showMessage(state.data.toString())
        }
    }

    private fun showEmptyView() {
        with(binding) {
            emptyView.root.visibility = View.VISIBLE
            recyclerItems.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun showRecyclerView() {
        with(binding) {
            emptyView.root.visibility = View.GONE
            recyclerItems.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }

    private fun showLoading() {
        with(binding) {
            emptyView.root.visibility = View.GONE
            recyclerItems.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun hideAllViews() {
        with(binding) {
            emptyView.root.visibility = View.GONE
            recyclerItems.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }
}
