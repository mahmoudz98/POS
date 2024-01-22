package com.casecode.pos.ui.signIn

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
/* import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode */
import com.casecode.domain.utils.Resource
import com.casecode.pos.databinding.FragmentLoginDialogBinding
import com.casecode.pos.viewmodel.AuthViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.mlkit.vision.barcode.BarcodeScanner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LoginDialogFragment : DialogFragment() {

    private val authViewModel: AuthViewModel by viewModels()

    private var _binding: FragmentLoginDialogBinding? = null
    private val binding get() = _binding!!

   // private lateinit var codeScanner: CodeScanner
    
    /**
     *  TODO: Add barcode scanner, It's very easy to use.
     * TODO: use barcode scanner with ml scanner in ml sdk kit.
     * // Example:
     *  https://github.com/googlesamples/mlkit/blob/master/android/vision-quickstart/app/src/main/java/com/google/mlkit/vision/demo/kotlin/barcodescanner/BarcodeScannerProcessor.kt
     *  // visit website to learn how to use :
     *  https://developers.google.com/ml-kit/vision/barcode-scanning/android
     *
     */
    private lateinit var barcodeScanner: BarcodeScanner
    
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestCameraPermissionOrStartScanning()
        //Issue: Bad way to declare variable  to pointer view in xml, use two data binding way,
        // 1. use binding.viewId
        // 2. don't use this way because it's not good practice, use binding.viewId
        // 3. Why, Its declared in ViewBinding class , and now its point to viewId in ViewBinding class, takes more space in memory, and this is shallow copy of view object.
        // Initialize views using binding
        val progressBar: ProgressBar = binding.progressBar
        val tilEmployeeId: TextInputLayout = binding.tilEmployeeId
        val tilPassword: TextInputLayout = binding.tilPassword
        val editTextEmail: TextInputEditText = binding.etEmployeeId
        val editTextPassword: TextInputEditText = binding.etPassword
        val buttonLogin: MaterialButton = binding.buttonLogin

        buttonLogin.setOnClickListener {
            val employeeId = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            // Show the progress bar
            progressBar.visibility = View.VISIBLE

            // Call the login function in your ViewModel
            // Pass employeeId and password as needed
            // Perform the employee login

            val uid = "5nIv3yXInpfJIhXMqjQKI9YSTN63"
            authViewModel.performEmployeeLogin(uid, employeeId, password)

            // Dismiss the dialog
            dismiss()
        }

        // Observe the login result
        lifecycleScope.launch {
            authViewModel.employeeLoginResult.collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // Handle success, you can access the employee data using result.data
                        // Dismiss the dialog or navigate to the next screen

                        val data = result.data
                        for (employee in data) {
                            Timber.i("employee name: ${employee.name}")
                        }
//                        dismiss()
                    }

                    is Resource.Error -> {
                        // Handle error, you can access the error message using result.message
                        // Display an error message to the user
                    }

                    is Resource.Loading -> {
                        // Handle loading state
                        // You might want to show a loading indicator
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            handleCameraPermissionResult(grantResults)
        }
    }

    private fun requestCameraPermissionOrStartScanning() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_DENIED
        ) {
            requestCameraPermission()
        } else {
            startScanning()
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    private fun handleCameraPermissionResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Camera permission granted
            Toast.makeText(requireContext(), "Camera permission granted", Toast.LENGTH_SHORT).show()
            startScanning()
        } else {
            // Camera permission denied
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startScanning() {
     /*    codeScanner = CodeScanner(requireContext(), binding.codeScannerView)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                lifecycleScope.launch {
                    // Process the scanned result using coroutines if needed
//                    viewModel.processScannedResult(it.text)
                    // Show a toast (or any other UI update)
                    UiThreadStatement.runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Scan result: ${it.text}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            errorCallback = ErrorCallback {
                UiThreadStatement.runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Camera initialization error: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.codeScannerView.setOnClickListener {
            codeScanner.startPreview()
        } */
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 123
    }
}