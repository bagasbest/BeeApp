package com.project.beeapp.ui.homepage.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.R
import com.project.beeapp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)


        Glide.with(requireActivity())
            .load("https://images.unsplash.com/photo-1470082719408-b2843ab5c9ab?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1333&q=10")
            .into(binding.bg)

        populateUserProfile()

        return binding.root
    }

    private fun populateUserProfile() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        FirebaseFirestore.
            getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                val username = "" + it.data?.get("username")
                val email = "" + it.data?.get("email")
                val phone = "" + it.data?.get("phone")
                val provinsi = "" + it.data?.get("locProvinsi")
                val kabupaten = "" + it.data?.get("locKabupaten")
                val kecamatan = "" + it.data?.get("locKecamatan")
                val kelurahan = "" + it.data?.get("locKelurahan")
                val image = "" + it.data?.get("image")

                binding.username.setText(username)
                binding.email.setText(email)
                binding.phone.setText(phone)
                binding.provinsi.setText(provinsi)
                binding.kabupaten.setText(kabupaten)
                binding.kecamatan.setText(kecamatan)
                binding.kelurahan.setText(kelurahan)


                if(image != "null") {
                    Glide.with(requireActivity())
                        .load(image)
                        .into(binding.image)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}