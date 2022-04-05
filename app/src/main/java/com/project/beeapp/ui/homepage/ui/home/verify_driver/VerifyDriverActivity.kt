package com.project.beeapp.ui.homepage.ui.home.verify_driver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.beeapp.R
import com.project.beeapp.databinding.ActivityVerifyDriverBinding

class VerifyDriverActivity : AppCompatActivity() {

    private var binding : ActivityVerifyDriverBinding? = null
    private var adapter: VerifyDriverAdapter? = null
    private var workStatus: String? = null

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel("all")
    }

    private fun initRecyclerView() {
        binding?.rvOrderProcess?.layoutManager = LinearLayoutManager(this)
        adapter = VerifyDriverAdapter("verify")
        binding?.rvOrderProcess?.adapter = adapter
    }

    private fun initViewModel(workStatus: String) {
        val viewModel = ViewModelProvider(this)[VerifyDriverViewModel::class.java]
        val role = intent.getStringExtra(ROLE)
        binding?.progressBar?.visibility = View.VISIBLE
        Log.e("taf", workStatus)

        if(role == "admin") {
            when (workStatus) {
                "all" -> {
                    viewModel.setListDriver()
                }
                "Mitra Standby" -> {
                    viewModel.setListDriverByStandby(false)
                }
                else -> {
                    viewModel.setListDriverByStandby(true)
                }
            }
        } else {
            val locationTask = intent.getStringArrayListExtra(LOCATION_TASK)
            when (workStatus) {
                "all" -> {
                    viewModel.setListDriverByLocationTask(locationTask)
                }
                "Mitra Standby" -> {
                    viewModel.setListDriverByLocationTaskAndWorkStatus(locationTask, false)
                }
                else -> {
                    viewModel.setListDriverByLocationTaskAndWorkStatus(locationTask, true)
                }
            }
        }

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

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.work_status, android.R.layout.simple_list_item_1
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding?.workStatus?.setAdapter(adapter)
        binding?.workStatus?.setOnItemClickListener { _, _, _, _ ->
            workStatus = binding?.workStatus?.text.toString()
            initRecyclerView()
            initViewModel(workStatus!!)
        }

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