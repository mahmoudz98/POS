package com.casecode.pos.ui.signout

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import coil.load
import com.casecode.pos.databinding.DialogSignOutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SignOutDialog : DialogFragment() {

    private lateinit var binding: DialogSignOutBinding

    interface SignOutDialogListener {
        fun onSignOut()
    }

    private var listener: SignOutDialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SignOutDialogListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.apply {
            // Get the string argument passed to the fragment and set it in the TextView

            // name
            val name = arguments?.getString("name")
            tvName.text = name

            // email
            val email = arguments?.getString("email")
            tvEmail.text = email

            // photo
            val photoUrl = arguments?.getString("photoUrl")
            ivPhoto.load(photoUrl)

            ivClose.setOnClickListener {
                dismiss()
            }

            btnSignOut.setOnClickListener {
                listener?.onSignOut()
                dismiss()
            }
        }

        isCancelable = false
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(
            requireContext()

        )
        binding = DialogSignOutBinding.inflate(layoutInflater)

        builder.setView(binding.root)
        builder.setCancelable(false)

        return builder.create()
    }

}