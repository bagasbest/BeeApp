package com.project.beeapp.ui.homepage.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.project.beeapp.databinding.FragmentOrderBinding

class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentOrderBinding.inflate(inflater, container, false)



        binding.tabs.addTab(binding.tabs.newTab().setText("Menunggu"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Proses"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Selesai"))

        val adapter = OrderPagerAdapter(this, childFragmentManager, binding.tabs.tabCount)

        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabs))
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}