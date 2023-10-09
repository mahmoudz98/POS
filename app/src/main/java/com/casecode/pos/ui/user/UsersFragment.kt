package com.casecode.pos.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.casecode.pos.databinding.FragmentUsersBinding
import com.casecode.pos.viewmodel.BusinessViewModel
import com.casecode.pos.viewmodel.UsersViewModel

class UsersFragment : Fragment() {


    private  var _binding: FragmentUsersBinding? = null
    val binding get() = _binding!!
    
    internal val businessViewModel by activityViewModels<BusinessViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        init()
        
    }
    private fun init(){
        initClick()
    }
    
    private fun initClick()
    {
        binding.apply {
            lUsers.btnUsersAdd.setOnClickListener{
                val userDialog = AddUserDialogFragment()
                userDialog.show(parentFragmentManager, AddUserDialogFragment.ADD_USER_TAG)
            }
            
            btnUsersSubscription.setOnClickListener{
                businessViewModel.moveToPreviousStep()
            }
            btnUsersDone.setOnClickListener{
            
            }
        }
        
    }
    
}