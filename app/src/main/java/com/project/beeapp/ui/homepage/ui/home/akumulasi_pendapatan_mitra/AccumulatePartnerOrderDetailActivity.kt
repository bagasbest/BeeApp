package com.project.beeapp.ui.homepage.ui.home.akumulasi_pendapatan_mitra

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.databinding.ActivityAccumulatePartnerOrderDetailBinding
import com.project.beeapp.ui.homepage.ui.home.verify_driver.VerifyDriverModel
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class AccumulatePartnerOrderDetailActivity : AppCompatActivity() {

    private var binding: ActivityAccumulatePartnerOrderDetailBinding? = null
    private var model: VerifyDriverModel? = null
    private val nominalCurrency: NumberFormat = DecimalFormat("#,###")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccumulatePartnerOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_DRIVER)
        Glide.with(this)
            .load(model?.image)
            .into(binding!!.image)

        binding?.status?.text = "Status: ${model?.status}"

        binding?.email?.text = "Email: ${model?.email}"
        binding?.fullname?.text = "Nama Lengkap: ${model?.fullname}"
        binding?.username?.text = "Username: ${model?.username}"
        binding?.phone?.text = "No.Handphone: ${model?.phone}"
        binding?.npwp?.text = "NPWP: ${model?.npwp}"

        getIncomeTotal()
        getIncomeMonthly()
        getIncomeDaily()

    }

    @SuppressLint("SetTextI18n")
    private fun getIncomeTotal() {
        FirebaseFirestore
            .getInstance()
            .collection("income")
            .whereEqualTo("partnerId", model?.uid)
            .get()
            .addOnSuccessListener { documents ->
                var totalIncome = 0L
                for(document in documents) {
                    totalIncome += document.data["income"] as Long
                }
                binding?.total?.text = "Pendapatan Total: Rp.${nominalCurrency.format(totalIncome)}"
            }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun getIncomeMonthly() {
        val df = SimpleDateFormat("MM")
        val currentMonth: String = df.format(Date())


        FirebaseFirestore
            .getInstance()
            .collection("income")
            .whereEqualTo("partnerId", model?.uid)
            .whereEqualTo("month", currentMonth)
            .get()
            .addOnSuccessListener { documents ->
                var monthlyIncome = 0L
                for(document in documents) {
                    monthlyIncome += document.data["income"] as Long
                }

                binding?.monthly?.text = "Pendapatan Bulan ini: Rp.${nominalCurrency.format(monthlyIncome)}"
            }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun getIncomeDaily() {
        val df = SimpleDateFormat("dd-MMM-yyyy")
        val currentDay: String = df.format(Date())


        FirebaseFirestore
            .getInstance()
            .collection("income")
            .whereEqualTo("partnerId", model?.uid)
            .whereEqualTo("date", currentDay)
            .get()
            .addOnSuccessListener { documents ->
                var dailyIncome = 0L
                for(document in documents) {
                    dailyIncome += document.data["income"] as Long
                }

                binding?.daily?.text = "Hari ini: Rp.${nominalCurrency.format(dailyIncome)}"
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_DRIVER = "driver"
    }
}