package com.casecode.pos.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.casecode.pos.ui.profile.ProfileInfoFragment
import com.casecode.pos.ui.profile.ProfilePlansFragment

class ProfilePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return  if (position == 1) {
            ProfilePlansFragment()
        } else ProfileInfoFragment()
    }

    override fun getItemCount(): Int {
        return ITEMS_PAGE_SIZE
    }

    companion object {
        private const val ITEMS_PAGE_SIZE = 2
    }
}
