package com.project.beeapp.ui.homepage.ui.home.aktifitas_konsumen

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.project.beeapp.R
import com.project.beeapp.databinding.ActivityUserDetailBinding
import com.project.beeapp.ui.homepage.ui.home.verify_driver.VerifyDriverModel
import com.project.beeapp.ui.homepage.ui.order.OrderAdapter
import com.project.beeapp.ui.homepage.ui.order.OrderViewModel

class UserDetailActivity : AppCompatActivity() {

    private var binding: ActivityUserDetailBinding? = null
    private var model: VerifyDriverModel? = null
    private var adapter: OrderAdapter? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_USER)

        if(model?.image != "null") {
            Glide.with(this)
                .load(model?.image)
                .into(binding!!.image)
        } else {
            Glide.with(this)
                .load(R.drawable.ic_baseline_face_24)
                .into(binding!!.image)
        }

        binding?.email?.text = "Email: ${model?.email}"
        binding?.fullname?.text = "Nama Lengkap: ${model?.fullname}"
        binding?.username?.text = "Username: ${model?.username}"
        binding?.phone?.text = "No.Handphone: ${model?.phone}"

        initRecyclerView()
        initViewModel()


        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding?.rvIncome?.layoutManager = layoutManager
        adapter = OrderAdapter()
        binding?.rvIncome?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[OrderViewModel::class.java]


        binding?.progressBarDriver?.visibility = View.VISIBLE
       viewModel.setLisOrderAllById(model?.uid)
        viewModel.getOrderList().observe(this) { order ->
            if (order.size > 0) {
                adapter!!.setData(order)
                binding?.noData?.visibility = View.GONE
            } else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding!!.progressBarDriver.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_USER = "user"
    }
}