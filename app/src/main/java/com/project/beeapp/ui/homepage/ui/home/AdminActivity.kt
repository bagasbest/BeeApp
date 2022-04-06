package com.project.beeapp.ui.homepage.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.databinding.ActivityAdminBinding
import com.project.beeapp.ui.homepage.ui.home.admin_daerah.AdminDaerahActivity
import com.project.beeapp.ui.homepage.ui.home.admin_daerah.AdminDaerahRegisterActivity
import com.project.beeapp.ui.homepage.ui.home.aktifitas_konsumen.UserActivity
import com.project.beeapp.ui.homepage.ui.home.akumulasi_pendapatan_mitra.AccumulatePartnerOrderActivity
import com.project.beeapp.ui.homepage.ui.home.bagi_hasil.BagiHasilActivity
import com.project.beeapp.ui.homepage.ui.home.rekening.RekeningActivity
import com.project.beeapp.ui.homepage.ui.home.verify_driver.VerifyDriverActivity

class AdminActivity : AppCompatActivity() {

    private var binding: ActivityAdminBinding? = null
    private var taskList = ArrayList<String>()
    private var role: String? = null
    override fun onResume() {
        super.onResume()
        checkRole()
    }

    private fun checkRole() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                role = "" + it.data!!["role"]

                if(role == "admin") {
                    binding?.bagiHasil?.visibility = View.VISIBLE
                    binding?.rekening?.visibility = View.VISIBLE
                    binding?.aktifitasKonsumenBtn?.visibility = View.VISIBLE
                    binding?.adminDaerah?.visibility = View.VISIBLE
                    binding?.daftarAdminDaerah?.visibility = View.VISIBLE
                    binding?.beeFloPoint?.visibility = View.VISIBLE
                    binding?.promoBtn?.visibility = View.VISIBLE
                } else  {
                    taskList.clear()
                    taskList.addAll(it.data!!["locationTask"] as ArrayList<String>)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.mitraBtn?.setOnClickListener {
            if(role == "admin") {
                val intent = Intent(this, VerifyDriverActivity::class.java)
                intent.putExtra(VerifyDriverActivity.ROLE, role)
                startActivity(intent)
            } else {
                val intent = Intent(this, VerifyDriverActivity::class.java)
                intent.putExtra(VerifyDriverActivity.ROLE, role)
                intent.putExtra(VerifyDriverActivity.LOCATION_TASK, taskList)
                startActivity(intent)
            }
        }

        binding?.akumulasiBtn?.setOnClickListener {
            if(role == "admin") {
                val intent = Intent(this, AccumulatePartnerOrderActivity::class.java)
                intent.putExtra(AccumulatePartnerOrderActivity.ROLE, role)
                startActivity(intent)
            } else {
                val intent = Intent(this, AccumulatePartnerOrderActivity::class.java)
                intent.putExtra(AccumulatePartnerOrderActivity.ROLE, role)
                intent.putExtra(AccumulatePartnerOrderActivity.LOCATION_TASK, taskList)
                startActivity(intent)
            }
        }

        binding?.bagiHasil?.setOnClickListener {
            startActivity(Intent(this, BagiHasilActivity::class.java))
        }

        binding?.rekening?.setOnClickListener {
            startActivity(Intent(this, RekeningActivity::class.java))
        }

        binding?.aktifitasKonsumenBtn?.setOnClickListener {
            startActivity(Intent(this, UserActivity::class.java))
        }

        binding?.adminDaerah?.setOnClickListener {
            startActivity(Intent(this, AdminDaerahRegisterActivity::class.java))
        }

        binding?.daftarAdminDaerah?.setOnClickListener {
            startActivity(Intent(this, AdminDaerahActivity::class.java))
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}