package com.casecode.pos.ui.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProfilePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val ITEMS_PAGE_SIZE = 2


    override fun createFragment(position: Int): Fragment {
        return  if (position == 1) {
            ProfilePlansFragment()
        } else ProfileInfoFragment()
    }

    override fun getItemCount(): Int {
        return ITEMS_PAGE_SIZE
    }
}
