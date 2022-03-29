package com.project.beeapp.ui.homepage.ui.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.R
import com.project.beeapp.databinding.FragmentNotificationBinding
import com.project.beeapp.ui.homepage.ui.order.OrderAdapter
import com.project.beeapp.ui.homepage.ui.order.OrderViewModel


class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private lateinit var adapter: NotificationAdapter
    val uid = FirebaseAuth.getInstance().currentUser!!.uid
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onResume() {
        super.onResume()
        checkRole()
    }

    private fun checkRole() {
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                val role = "" + it.data?.get("role")
                initRecyclerView()
                when (role) {
                    "user" -> {
                        initViewModel(role)
                    }
                    "driver" -> {
                        initViewModel(role)
                    }
                    else -> {
                        initViewModel(role)
                    }
                }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)

        Glide.with(requireActivity())
            .load(R.drawable.logo_trans2)
            .into(binding.imageView3)

        return binding.root
    }


    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        binding.rvNotification.layoutManager = layoutManager
        adapter = NotificationAdapter()
        binding.rvNotification.adapter = adapter
    }

    private fun initViewModel(role: String) {
        val viewModel = ViewModelProvider(this)[NotificationViewModel::class.java]


        binding.progressBar.visibility = View.VISIBLE
        when (role) {
            "user" -> {
                viewModel.setLisNotificationByUser(uid)
            }
            "driver" -> {
                viewModel.setLisNotificationByMitra(uid)
            }
            else -> {
                viewModel.setLisNotificationByAdmin()
            }
        }
        viewModel.getNotificationList().observe(this) { notificationList ->
            if (notificationList.size > 0) {
                adapter.setData(notificationList)
                binding.noData.visibility = View.GONE
            } else {
                binding.noData.visibility = View.VISIBLE
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}