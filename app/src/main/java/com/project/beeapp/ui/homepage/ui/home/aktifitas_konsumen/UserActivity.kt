package com.project.beeapp.ui.homepage.ui.home.aktifitas_konsumen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.beeapp.databinding.ActivityUserBinding
import com.project.beeapp.ui.homepage.ui.home.verify_driver.VerifyDriverAdapter
import com.project.beeapp.ui.homepage.ui.home.verify_driver.VerifyDriverViewModel

class UserActivity : AppCompatActivity() {

    private var binding: ActivityUserBinding? = null
    private var adapter: VerifyDriverAdapter? = null


    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel()
    }

    private fun initRecyclerView() {
        binding?.rvCustomer?.layoutManager = LinearLayoutManager(this)
        adapter = VerifyDriverAdapter("user")
        binding?.rvCustomer?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[VerifyDriverViewModel::class.java]


        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setListUser()
        viewModel.getDriverList().observe(this) { driverList ->
            if (driverList.size > 0) {
                adapter!!.setData(driverList)
                binding?.noData?.visibility = View.GONE
            } else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding!!.progressBar.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}