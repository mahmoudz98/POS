package com.casecode.pos.ui.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.casecode.domain.model.users.Item
import com.casecode.pos.R
import com.casecode.pos.adapter.ItemsAdapter
import com.casecode.pos.databinding.FragmentItemsBinding
import com.casecode.pos.utils.setupSnackbar
import com.casecode.pos.viewmodel.ItemsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for displaying a list of items.
 */
@AndroidEntryPoint
class ItemsFragment : Fragment() {
    private var _binding: FragmentItemsBinding? = null
    private val binding: FragmentItemsBinding get() = _binding!!
    private val viewModel: ItemsViewModel by viewModels()
    private val itemAdapter = ItemsAdapter(
        onItemClick = { item ->
            viewModel.setItemSelected(item)
            showItemDialog(ItemDialogFragment.ITEM_UPDATE_FRAGMENT) },
        onItemLongClick = { item -> deleteItem(item = item) },
        onPrintButtonClick = { item ->
            viewModel.setItemSelected(item)
            showQRCodeDialog() },)

    private var menuProvider: MenuProvider? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentItemsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner
        setup()
    }

    private fun setup() {
        setupMenu()
        setupRecyclerView()
        binding.floatingBtnItems.setOnClickListener { showItemDialog(ItemDialogFragment.ITEM_ADD_FRAGMENT) }
        setupObserver()
        setupSnackbar()
    }

    private fun setupMenu() {
        menuProvider = object : MenuProvider {
            override fun onCreateMenu(
                menu: Menu,
                menuInflater: MenuInflater, ) {
                menuInflater.inflate(R.menu.menu_items_fragment, menu)
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView
                searchView.queryHint = getString(R.string.search_hint)

                searchView.setOnQueryTextListener(
                    object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            newText?.let { itemAdapter.filterItems(it) }
                            return true
                        }
                    },
                )
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_search -> {
                        true
                    }
                    else -> false
                }
            }
        }
        requireActivity().addMenuProvider(menuProvider!!)
    }

    private fun setupRecyclerView() {
        binding.recyclerItems.adapter = itemAdapter
    }

    private fun setupObserver() {
        viewModel.fetchItems()
        // is loading
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.isLoading = it
        }
        viewModel.items.observe(viewLifecycleOwner) { items ->
            binding.items = items
        }
        viewModel.isEmptyItems.observe(viewLifecycleOwner) {
            binding.isEmpty = it
        }
    }

    private fun showItemDialog(tag: String) {
         ItemDialogFragment.newInstance().show(childFragmentManager, tag)
    }

    private fun deleteItem(item: Item) {
        val builder =
            MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.delete_item_title)
                .setMessage(R.string.delete_item_message)
                .setPositiveButton(R.string.delete) { _, _ ->
                    viewModel.checkNetworkAndDeleteItem(item)
                }.setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
        builder.create().show()
    }

    private fun showQRCodeDialog() {
        QRCodeDialogFragment.newInstance().show(childFragmentManager, null)
    }

    private fun setupSnackbar() {
        binding.root.setupSnackbar(viewLifecycleOwner, viewModel.userMessage, Snackbar.LENGTH_LONG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        menuProvider?.let { requireActivity().removeMenuProvider(it) }
        _binding = null
        viewModelStore.clear()


    }


}