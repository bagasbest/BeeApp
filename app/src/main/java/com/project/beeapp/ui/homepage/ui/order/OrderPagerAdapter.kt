package com.project.beeapp.ui.homepage.ui.order

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.project.beeapp.ui.homepage.ui.order.status.OrderFinishFragment
import com.project.beeapp.ui.homepage.ui.order.status.OrderPickFragment
import com.project.beeapp.ui.homepage.ui.order.status.OrderProcessFragment

class OrderPagerAdapter(var context: OrderFragment, fm: FragmentManager, var totalTabs: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                OrderPickFragment()
            }
            1 -> {
                OrderProcessFragment()
            }
            2 -> {
                OrderFinishFragment()
            }
            else -> getItem(position)
        }
    }
    override fun getCount(): Int {
        return totalTabs
    }
}