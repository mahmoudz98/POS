package com.casecode.pos.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.casecode.pos.ui.profile.ProfileBranchesFragment
import com.casecode.pos.ui.profile.ProfileBusinessFragment
import com.casecode.pos.ui.profile.ProfilePlansFragment

class ProfilePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return  when (position)  {
            0 -> ProfileBusinessFragment()
                1 -> ProfileBranchesFragment()

            else -> ProfilePlansFragment()
        }
    }

    override fun getItemCount(): Int {
        return ITEMS_PAGE_SIZE
    }

    companion object {
        private const val ITEMS_PAGE_SIZE = 3
    }
}
