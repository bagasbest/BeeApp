package com.project.beeapp.ui.homepage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.beeapp.R
import com.project.beeapp.databinding.ActivityHomeBinding
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import com.project.beeapp.ui.homepage.ui.home.HomeFragment
import com.project.beeapp.ui.homepage.ui.order.OrderFragment
import com.project.beeapp.ui.homepage.ui.profile.ProfileFragment


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView = findViewById<ChipNavigationBar>(R.id.nav_view)

        navView.setItemSelected(R.id.navigation_home, true)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, HomeFragment()).commit()


        bottomMenu(navView)

    }

    @SuppressLint("NonConstantResourceId")
    private fun bottomMenu(navView: ChipNavigationBar) {
        navView.setOnItemSelectedListener { i: Int ->
            var fragment: Fragment? = null
            when (i) {
                R.id.navigation_home -> fragment = HomeFragment()
                R.id.navigation_order -> fragment = OrderFragment()
                R.id.navigation_profile -> fragment = ProfileFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    fragment!!
                ).commit()
        }
    }
}