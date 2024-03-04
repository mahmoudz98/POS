package com.casecode.pos.ui.signIn

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.base.PositiveDialogListener
import com.casecode.pos.base.doAfterTextChangedListener
import com.casecode.pos.databinding.FragmentLoginDialogBinding
import com.casecode.pos.ui.main.MainActivity
import com.casecode.pos.ui.permissions.PermissionRequestCameraDialog
import com.casecode.pos.utils.setupSnackbar
import com.casecode.pos.utils.showSnackbar
import com.casecode.pos.viewmodel.AuthViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginDialogFragment : DialogFragment(), PositiveDialogListener {
    private val authViewModel: AuthViewModel by viewModels()

    @Suppress("ktlint:standard:property-naming")
    private var _binding: FragmentLoginDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner

    private val requestCameraPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, proceed with camera operations
                handleCameraPermissionGranted()
            } else {
                // Permission denied, handle the denial
                handleCameraPermissionDenied()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginDialogBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        requestCameraPermissionOrStartScanning()
        validateLoginEmployeeInput()
        binding.btnLogin.setOnClickListener {
            // Show the progress bar
            if (isValidEmployeeInput()) {
                binding.pgbLogin.visibility = View.VISIBLE
                authViewModel.performEmployeeLogin()
            }
        }
        setupSnackbar()
        observerIsEmployeeLoginSuccess()
    }

    private fun handleCameraPermissionGranted() {
        startScanning()
    }

    private fun requestCameraPermissionOrStartScanning() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED -> {
                startScanning()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA,
            ) -> {
                handleCameraPermissionDenied()
            }

            else -> {
                requestCameraPermission()
            }
        }
    }

    private fun startScanning() {
        codeScanner = CodeScanner(requireActivity(), binding.codeScannerLogin)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback =
                DecodeCallback {
                    lifecycleScope.launch {
                        // Process the scanned result using coroutines if needed
                        authViewModel.processScannedResult(it.text)

                        binding.root.showSnackbar(
                            getString(R.string.message_scan_complete),
                            Snackbar.LENGTH_SHORT,
                        )
                    }
                }
            errorCallback =
                ErrorCallback {
                    binding.root.showSnackbar(
                        "${getString(R.string.message_scan_error)} ${it.message}",
                        Snackbar.LENGTH_SHORT,
                    )
                }
        }

        binding.codeScannerLogin.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun handleCameraPermissionDenied() {
        val permissionDialog = PermissionRequestCameraDialog()
        permissionDialog.listener = this
        permissionDialog.show(childFragmentManager, "PermissionRequestCameraDialog")

        dialog?.setCanceledOnTouchOutside(false)
        permissionDialog.dialog?.setOnDismissListener {
            dialog?.setCanceledOnTouchOutside(true)
        }
    }

    private fun requestCameraPermission() {
        requestCameraPermission.launch(Manifest.permission.CAMERA)
    }

    private fun validateLoginEmployeeInput() {
        binding.etLoginName.doAfterTextChangedListener { nameEditText ->
            if (TextUtils.isEmpty(nameEditText)) {
                binding.tilLoginName.boxStrokeErrorColor
                binding.tilLoginName.error =
                    getString(R.string.add_employee_name_empty)
            } else {
                binding.tilLoginName.boxStrokeColor =
                    resources.getColor(R.color.md_theme_light_primary, requireActivity().theme)
                binding.tilLoginName.error = null
            }
        }
        binding.etLoginPassword.doAfterTextChangedListener { passwordEditText ->

            if (TextUtils.isEmpty(passwordEditText)) {
                binding.tilLoginPassword.boxStrokeErrorColor
                binding.tilLoginPassword.error =
                    getString(R.string.add_employee_password_empty)
            } else if (passwordEditText.toString().length < 6) {
                binding.tilLoginPassword.error =
                    getString(R.string.add_employee_password_error)
            } else {
                binding.tilLoginPassword.boxStrokeColor =
                    resources.getColor(R.color.md_theme_light_primary, requireActivity().theme)
                binding.tilLoginPassword.error = null
            }
        }
    }

    private fun observerIsEmployeeLoginSuccess() {
        authViewModel.isEmployeeLoginSuccess.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.pgbLogin.visibility = View.VISIBLE
                }

                is Resource.Empty -> {
                    binding.pgbLogin.visibility = View.INVISIBLE
                }

                is Resource.Error -> {
                    binding.pgbLogin.visibility = View.INVISIBLE
                    binding.root.showSnackbar(
                        getString(R.string.login_employee_error),
                        Snackbar.LENGTH_SHORT,
                    )
                }

                is Resource.Success -> {
                    binding.pgbLogin.visibility = View.INVISIBLE
                    if (it.data) {
                        dismiss()
                        moveToMainActivity()
                    } else {
                        binding.root.showSnackbar(
                            getString(R.string.login_employee_incorrect),
                            Snackbar.LENGTH_SHORT,
                        )
                    }
                }
            }
        }
    }

    private fun moveToMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP // used to clean activity and al activities above it will be removed.
        startActivity(intent)
    }

    private fun isValidEmployeeInput(): Boolean {
        val name = binding.etLoginName.text.toString()
        val password = binding.etLoginPassword.text.toString()
        // Check login and pass are empty
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password) || password.length < 6) {
            if (TextUtils.isEmpty(name)) {
                binding.tilLoginName.error =
                    getString(R.string.add_employee_name_empty)
            }
            if (TextUtils.isEmpty(password)) {
                binding.tilLoginPassword.error =
                    getString(R.string.add_employee_password_empty)
            }

            if (password.length < 6) {
                binding.tilLoginPassword.error = getString(R.string.add_employee_password_error)
                return false
            }
            return false
        }

        authViewModel.setEmployeeLogin(name, password)
        return true
    }

    private fun setupSnackbar() {
        binding.root.setupSnackbar(
            viewLifecycleOwner,
            authViewModel.userMessage,
            BaseTransientBottomBar.LENGTH_SHORT,
        )
    }

    override fun onDialogPositiveClick() {
        requestCameraPermission()
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        if (::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}