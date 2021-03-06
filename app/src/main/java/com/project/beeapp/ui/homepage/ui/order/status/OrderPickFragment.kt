package com.project.beeapp.ui.homepage.ui.order.status

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.databinding.FragmentOrderPickBinding
import com.project.beeapp.ui.homepage.ui.order.OrderAdapter
import com.project.beeapp.ui.homepage.ui.order.OrderViewModel

class OrderPickFragment : Fragment() {

    private var binding: FragmentOrderPickBinding? = null
    private var adapter: OrderAdapter? = null
    private var role: String? = null
    private val myUID = FirebaseAuth.getInstance().currentUser!!.uid
    private var locationTask = ArrayList<String>()

    private var driverLocKecamatan: String? = null
    private var driverLocKelurahan: String? = null

    override fun onResume() {
        super.onResume()

        checkRole()

    }

    @SuppressLint("SetTextI18n")
    private fun checkRole() {
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(myUID)
            .get()
            .addOnSuccessListener {
                role = it.data?.get("role").toString()
                driverLocKecamatan = it.data?.get("locKecamatan").toString()
                driverLocKelurahan = it.data?.get("locKelurahan").toString()
                val isWork = it.data?.get("isWork") as Boolean

                if(!isWork) {
                    initRecyclerView()
                    initViewModel()
                } else {
                    binding?.noData?.visibility = View.VISIBLE
                    binding?.rvOrderProcess?.visibility = View.GONE
                    binding?.noData?.text = "Anda Sedang Berkerja"
                }

                if(role == "adminKecamatan") {
                    locationTask.addAll(it.data!!["locationTask"] as ArrayList<String>)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderPickBinding.inflate(inflater, container, false)

        return binding?.root
    }

    private fun initRecyclerView() {
        binding?.rvOrderProcess?.layoutManager = LinearLayoutManager(activity)
        adapter = OrderAdapter()
        binding?.rvOrderProcess?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[OrderViewModel::class.java]


        binding?.progressBar?.visibility = View.VISIBLE
        when (role) {
            "user" -> {
                viewModel.setListOrderProcessByPick(myUID)
            }
            "driver" -> {
                viewModel.setListOrderProcessByDriver(driverLocKecamatan)
            }
            "admin" -> {
                viewModel.setListOrderProcessByAdmin()
            }
            "adminKecamatan" -> {
                viewModel.setListOrderProcessByAdminKecamatan(locationTask)
            }
        }
        viewModel.getOrderList().observe(this) { orderPick ->

            if (orderPick.size > 0) {
                adapter!!.setData(orderPick)
                binding?.noData?.visibility = View.GONE
            } else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding!!.progressBar.visibility = View.GONE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}