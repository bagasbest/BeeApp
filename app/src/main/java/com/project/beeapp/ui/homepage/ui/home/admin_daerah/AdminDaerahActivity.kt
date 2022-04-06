package com.project.beeapp.ui.homepage.ui.home.admin_daerah

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.beeapp.databinding.ActivityAdminDaerahBinding

class AdminDaerahActivity : AppCompatActivity() {

    private var binding: ActivityAdminDaerahBinding? = null
    private var adapter: AdminAdapter? = null


    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel()
    }

    private fun initRecyclerView() {
        binding?.rvAdmin?.layoutManager = LinearLayoutManager(this)
        adapter = AdminAdapter()
        binding?.rvAdmin?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[AdminViewModel::class.java]


        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setListAdmin()
        viewModel.getAdminList().observe(this) { adminKecamatan ->
            if (adminKecamatan.size > 0) {
                adapter!!.setData(adminKecamatan)
                binding?.noData?.visibility = View.GONE
            } else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding!!.progressBar.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDaerahBinding.inflate(layoutInflater)
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