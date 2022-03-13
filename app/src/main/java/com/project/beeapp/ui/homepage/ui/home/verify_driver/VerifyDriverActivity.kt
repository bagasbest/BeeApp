package com.project.beeapp.ui.homepage.ui.home.verify_driver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.beeapp.databinding.ActivityVerifyDriverBinding

class VerifyDriverActivity : AppCompatActivity() {

    private var binding : ActivityVerifyDriverBinding? = null
    private var adapter: VerifyDriverAdapter? = null

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel()
    }

    private fun initRecyclerView() {
        binding?.rvOrderProcess?.layoutManager = LinearLayoutManager(this)
        adapter = VerifyDriverAdapter("verify")
        binding?.rvOrderProcess?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[VerifyDriverViewModel::class.java]


        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setListDriver()
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
        binding = ActivityVerifyDriverBinding.inflate(layoutInflater)
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