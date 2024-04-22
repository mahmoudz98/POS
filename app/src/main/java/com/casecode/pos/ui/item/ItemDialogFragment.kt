package com.casecode.pos.ui.item

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.casecode.domain.model.users.Item
import com.casecode.pos.R
import com.casecode.pos.base.PositiveDialogListener
import com.casecode.pos.databinding.DialogItemBinding
import com.casecode.pos.ui.permissions.PermissionRequestCameraDialog
import com.casecode.pos.utils.EventObserver
import com.casecode.pos.utils.showSnackbar
import com.casecode.pos.utils.startScanningBarcode
import com.casecode.pos.viewmodel.ItemsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * A dialog fragment for adding or updating an item.
 */
@AndroidEntryPoint
class ItemDialogFragment : DialogFragment() {
    // View binding for the dialog layout
    private var _binding: DialogItemBinding? = null
    val binding get() = _binding!!

    // ViewModel instance
    private val viewModel: ItemsViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
    )
    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // Permission denied, handle the denial
            handleCameraPermissionDenied()
        }
    }
    private lateinit var currentPhotoPath: String
    private val imageCaptureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->

            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data != null && it.data?.data != null) {
                    val imageUri = it.data?.data
                    binding.imvItem.setImageURI(imageUri)
                } else {
                    FileProvider.getUriForFile(
                        requireContext(),
                        requireActivity().applicationContext.packageName + ".fileprovider",
                        File(currentPhotoPath),
                    )?.let { url ->
                        binding.imvItem.setImageURI(url)
                    }

                    Timber.d("RESULT DATA: ${it.data?.data}")
                }
                updatedImageItem()
            } else if (it.resultCode == Activity.RESULT_CANCELED) {
                binding.root.showSnackbar(
                    getString(R.string.item_no_image_selected),
                    Snackbar.LENGTH_SHORT,
                )
                Timber.i("result for take image or gallery is null")
            }
        }

    private fun updatedImageItem() {
        if (tag == ITEM_UPDATE_FRAGMENT) viewModel.updateItemImage()
    }

    private val barLauncher = registerForActivityResult(ScanContract()) { result ->
        result.contents.let {
            if (it == null) {
                binding.root.showSnackbar(
                    getString(R.string.message_scan_error),
                    Snackbar.LENGTH_SHORT,
                )
            } else {
                // Handle the scanned barcode result
                binding.tilBarcode.editText?.setText(it)
                binding.tilBarcode.editText?.onEditorAction(EditorInfo.IME_ACTION_DONE)
                // Simulate pressing "Done"
            }
        }
    }

    /**
     * Creates the dialog with the dialog layout.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext())
        _binding = DialogItemBinding.inflate(layoutInflater)
        return builder.setView(binding.root).create()
    }

    /**
     * Inflates the dialog layout.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    /**
     * Initializes the views and sets up click listeners.
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this.viewLifecycleOwner
        setup()
    }

    private fun setup() {
        observerLoadingItem()
        setupObserverUpdateItem()
        setupClick()

    }

    private fun observerLoadingItem() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.isLoading = it
        }
    }

    private fun setupObserverUpdateItem() {
        if (tag == ITEM_UPDATE_FRAGMENT) {
            viewModel.itemSelected.observe(viewLifecycleOwner) {
                binding.item = it
            }
        }
    }

    /**
     * Sets up the views based on whether it's an add or update operation.
     */
    private fun setupClick() {
        // Set click listener for image view to capture image
        binding.imvItem.setOnClickListener {
            requestCameraPermissionOrStartCapture()
        }
        // Set click listener for image view to scan barcode
        binding.imageButtonScanBarcode.setOnClickListener { startScanBarcode() }

        // Set click listener for item button to add or update item
        binding.btnItem.setOnClickListener {
            if (isValidItemInput()) {
                if (tag == ITEM_ADD_FRAGMENT) {
                    viewModel.checkNetworkAndAddItem()
                } else {
                    viewModel.checkNetworkAndUpdateItem()
                }
                viewModel.isAddItem.observe(
                    viewLifecycleOwner,
                    EventObserver { isAddOrUpdateItem ->
                        if (isAddOrUpdateItem) {
                            dismiss()
                        }
                    },
                )
            }

        }
    }

    private fun requestCameraPermissionOrStartCapture() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            captureImageFromCameraOrGallery()
        } else {
            handleCameraPermissionDenied()
        }
    }

    private fun handleCameraPermissionDenied() {
        val permissionDialog = PermissionRequestCameraDialog()
        permissionDialog.listener = PositiveDialogListener {
            requestCameraPermission()
        }
        permissionDialog.show(childFragmentManager, "PermissionRequestCameraDialog")

        dialog?.setCanceledOnTouchOutside(false)
        permissionDialog.dialog?.setOnDismissListener {
            dialog?.setCanceledOnTouchOutside(true)
        }
    }

    private fun requestCameraPermission() {
        requestCameraPermission.launch(Manifest.permission.CAMERA)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return with(File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)) {
            currentPhotoPath = absolutePath
            this
        }
    }

    /**
     * Initiates the capture of an image from camera or gallery.
     */
    private fun captureImageFromCameraOrGallery() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Timber.e(ex)
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        requireActivity().applicationContext.packageName + ".fileprovider",
                        it,
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                }
            }
        }

        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        // Create a chooser intent to let the user select between camera and gallery
        val chooserIntent = Intent.createChooser(pickPhoto, getString(R.string.select_image))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePicture))
        imageCaptureLauncher.launch(chooserIntent)

    }

    /**
     * Validates the input fields for item details.
     *
     * @return `true` if the input is valid, `false` otherwise.
     */
    private fun isValidItemInput(): Boolean {
        // Get input values from text fields
        val name = binding.tilName.editText?.text.toString().trim()
        val price = binding.tilPrice.editText?.text.toString().trim()
        val quantity = binding.tilQuantity.editText?.text.toString().trim()
        val barcode = binding.tilBarcode.editText?.text.toString().trim()

        // Check if any required field is empty and show error messages if necessary
        if (name.isEmpty() || price.isEmpty() || quantity.isEmpty() || barcode.isEmpty()) {
            if (name.isEmpty()) {
                binding.tilName.error = getString(R.string.name_is_required)
            }
            if (price.isEmpty()) {
                binding.tilPrice.error = getString(R.string.price_is_required)
            }
            if (quantity.isEmpty()) {
                binding.tilQuantity.error = getString(R.string.quantity_is_required)
            }
            if (barcode.isEmpty()) {
                binding.tilBarcode.error = getString(R.string.barcode_is_required)
            }
            return false
        }
        // Set bitmap to ViewModel if available
        with(binding.imvItem.drawable as? BitmapDrawable) {
            viewModel.setBitmap(this?.bitmap)
        }
        // Create the item object
        val item = Item(
            name = name,
            price = price.toDouble(),
            quantity = quantity.toDouble(),
            sku = barcode,
            unitOfMeasurement = null,
            imageUrl = null,
        )
        // Set the item to the ViewModel
        if (tag == ITEM_ADD_FRAGMENT) {
            viewModel.setItemSelected(item)
        } else {
            viewModel.setItemUpdated(item)
        }

        return true
    }

    private fun startScanBarcode() {
        barLauncher.launch(ScanOptions().startScanningBarcode(requireContext()),)
    }

    /**
     * Clears the view binding instance when the view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        imageCaptureLauncher.unregister()
        _binding = null
    }

    /**
     * Companion object containing constants and a factory method to create instances of [ItemDialogFragment].
     */
    companion object {
        const val ITEM_ADD_FRAGMENT = "ITEM_ADD_FRAGMENT"
        const val ITEM_UPDATE_FRAGMENT = "ITEM_UPDATE_FRAGMENT"

        fun newInstance(
        ): ItemDialogFragment {
            return ItemDialogFragment()
        }
    }
}