package com.project.beeapp.ui.homepage.ui.home.akumulasi_pendapatan_mitra

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.beeapp.databinding.ActivityAccumulatePartnerOrderBinding
import com.project.beeapp.ui.homepage.ui.home.verify_driver.VerifyDriverActivity
import com.project.beeapp.ui.homepage.ui.home.verify_driver.VerifyDriverAdapter
import com.project.beeapp.ui.homepage.ui.home.verify_driver.VerifyDriverViewModel

class AccumulatePartnerOrderActivity : AppCompatActivity() {

    private var binding: ActivityAccumulatePartnerOrderBinding? = null
    private lateinit var adapter: VerifyDriverAdapter

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel()
    }

    private fun initRecyclerView() {
        binding?.rvIncome?.layoutManager = LinearLayoutManager(this)
        adapter = VerifyDriverAdapter("accumulate")
        binding?.rvIncome?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[VerifyDriverViewModel::class.java]
        val role = intent.getStringExtra(VerifyDriverActivity.ROLE)
        binding?.progressBar?.visibility = View.VISIBLE

        if(role == "admin") {
            viewModel.setListDriver()
        } else {
            val locationTask = intent.getStringArrayListExtra(VerifyDriverActivity.LOCATION_TASK)
            viewModel.setListDriverByLocationTask(locationTask)
        }
        viewModel.getDriverList().observe(this) { income ->
            if (income.size > 0) {
                adapter.setData(income)
                binding?.noData?.visibility = View.GONE
            } else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding!!.progressBar.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccumulatePartnerOrderBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val ROLE = "role"
        const val LOCATION_TASK = "lt"
    }
}