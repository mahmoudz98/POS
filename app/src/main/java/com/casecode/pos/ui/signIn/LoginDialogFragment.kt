package com.casecode.pos.ui.signIn

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.base.doAfterTextChangedListener
import com.casecode.pos.databinding.FragmentLoginDialogBinding
import com.casecode.pos.ui.main.MainActivity
import com.casecode.pos.utils.setupSnackbar
import com.casecode.pos.utils.showSnackbar
import com.casecode.pos.utils.startScanningBarcode
import com.casecode.pos.viewmodel.AuthViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginDialogFragment : DialogFragment() {
    private val authViewModel: AuthViewModel by viewModels()

    @Suppress("ktlint:standard:property-naming")
    private var _binding: FragmentLoginDialogBinding? = null
    private val binding get() = _binding!!
    private val barLauncher = registerForActivityResult(ScanContract()) { result ->
        result.contents.let {
            if (it == null) {
                binding.root.showSnackbar(
                    getString(R.string.message_scan_error),
                    Snackbar.LENGTH_SHORT,
                )
            } else {
                // Handle the scanned barcode result
                binding.tilLoginScanUid.editText?.setText(it)
            }
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
        setup()

    }

    private fun setup() {
        validateLoginEmployeeInput()
        setupClick()
        setupSnackbar()
        observerIsEmployeeLoginSuccess()
    }

    private fun setupClick() {
        binding.btnLoginScanUid.setOnClickListener { startScanBarcode() }

        binding.btnLogin.setOnClickListener {
            // Show the progress bar
            if (isValidEmployeeInput()) {
                binding.pgbLogin.visibility = View.VISIBLE
                authViewModel.performEmployeeLogin()
            }
        }
    }

    private fun startScanBarcode() {
        barLauncher.launch(ScanOptions().startScanningBarcode(requireContext()))
    }

    private fun validateLoginEmployeeInput() {
        binding.etLoginName.doAfterTextChangedListener { nameEditText ->
            if (TextUtils.isEmpty(nameEditText)) {
                binding.tilLoginName.boxStrokeErrorColor
                binding.tilLoginName.error = getString(R.string.add_employee_name_empty)
            } else {
                binding.tilLoginName.boxStrokeColor =
                    resources.getColor(R.color.md_theme_light_primary, requireActivity().theme)
                binding.tilLoginName.error = null
            }
        }
        binding.etLoginPassword.doAfterTextChangedListener { passwordEditText ->

            if (TextUtils.isEmpty(passwordEditText)) {
                binding.tilLoginPassword.boxStrokeErrorColor
                binding.tilLoginPassword.error = getString(R.string.add_employee_password_empty)
            } else if (passwordEditText.toString().length < 6) {
                binding.tilLoginPassword.error = getString(R.string.add_employee_password_error)
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
        val uid = binding.tilLoginScanUid.editText?.text.toString()
        // Check login and pass are empty
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(name) || TextUtils.isEmpty(password) || password.length < 6) {
            if (TextUtils.isEmpty(uid)) {
                binding.tilLoginScanUid.error = getString(R.string.login_employee_uid_empty)
            }
            if (TextUtils.isEmpty(name)) {
                binding.tilLoginName.error = getString(R.string.add_employee_name_empty)
            }
            if (TextUtils.isEmpty(password)) {
                binding.tilLoginPassword.error = getString(R.string.add_employee_password_empty)
            }

            if (password.length < 6) {
                binding.tilLoginPassword.error = getString(R.string.add_employee_password_error)
                return false
            }
            return false
        }

        authViewModel.setEmployeeLogin(uid, name, password)
        return true
    }

    private fun setupSnackbar() {
        binding.root.setupSnackbar(
            viewLifecycleOwner,
            authViewModel.userMessage,
            BaseTransientBottomBar.LENGTH_SHORT,
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}