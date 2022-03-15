package com.project.beeapp.ui.homepage.ui.home.beefuel

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.R
import com.project.beeapp.databinding.ActivityBeeFuelBinding
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule


class BeeFuelActivity : AppCompatActivity() {

    private var binding: ActivityBeeFuelBinding? = null
    private val myUid = FirebaseAuth.getInstance().currentUser!!.uid
    private var provinsi: String? = null
    private var kabupaten: String? = null
    private var kecamatan: String? = null
    private var kelurahan: String? = null
    private var name: String? = null
    private var priceTotal: Long? = 0L
    private var fuelType: String? = null
    private val format: NumberFormat = DecimalFormat("#,###")
    private var model = BeeFuelModel()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeeFuelBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        checkRole()
        getPricing()

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.fuel_type, android.R.layout.simple_list_item_1
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding?.fuelType?.setAdapter(adapter)
        binding?.fuelType?.setOnItemClickListener { _, _, _, _ ->
            fuelType = binding?.fuelType?.text.toString()

            when (fuelType) {
                "Dex" -> {
                    priceTotal = model.dex
                    binding?.priceTotal?.text = "Rp.${format.format(model.dex)}"
                }
                "Dex Lite" -> {
                    priceTotal = model.dexLite
                    binding?.priceTotal?.text = "Rp.${format.format(model.dexLite)}"
                }
                "Pertalite" -> {
                    priceTotal = model.pertalite
                    binding?.priceTotal?.text = "Rp.${format.format(model.pertalite)}"
                }
                "Pertamax" -> {
                    priceTotal = model.pertamax
                    binding?.priceTotal?.text = "Rp.${format.format(model.pertamax)}"
                }
                "Pertamax Turbo" -> {
                    priceTotal = model.pertamaxTurbo
                    binding?.priceTotal?.text = "Rp.${format.format(model.pertamaxTurbo)}"
                }
                "Premium" -> {
                    priceTotal = model.premium
                    binding?.priceTotal?.text = "Rp.${format.format(model.premium)}"
                }
                "Solar" -> {
                    priceTotal = model.solar
                    binding?.priceTotal?.text = "Rp.${format.format(model.solar)}"
                }
            }
        }




        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.orderBtn?.setOnClickListener {
            formValidation()
        }

        binding?.edit?.setOnClickListener {
            val intent = Intent (this, BeeFuelEditActivity::class.java)
            intent.putExtra(BeeFuelEditActivity.EXTRA_PRICE_FUEL, model)
            startActivity(intent)
        }


    }

    private fun getPricing() {
        FirebaseFirestore
            .getInstance()
            .collection("pricing")
            .document("beeFuel")
            .get()
            .addOnSuccessListener {
                if(it.exists()) {
                    model.dex = it.data?.get("Dex") as Long
                    model.dexLite = it.data?.get("Dex Lite") as Long
                    model.pertalite = it.data?.get("Pertalite") as Long
                    model.pertamax = it.data?.get("Pertamax") as Long
                    model.pertamaxTurbo = it.data?.get("Pertamax Turbo") as Long
                    model.premium = it.data?.get("Premium") as Long
                    model.solar = it.data?.get("Solar") as Long
                }
            }
    }

    private fun checkRole() {
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(myUid)
            .get()
            .addOnSuccessListener {
                if (it.data?.get("role") == "admin") {
                    binding?.edit?.visibility = View.VISIBLE
                }
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun formValidation() {
        val address = binding?.address?.text.toString().trim()
        val liter = binding?.liter?.text.toString().trim()
        when {
            address.isEmpty() -> {
                Toast.makeText(this, "Alamat lengkap tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            liter.isEmpty() -> {
                Toast.makeText(this, "Tentukan berapa liter yang anda inginkan", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            liter.toInt() < 3 -> {
                Toast.makeText(this, "Minimal order 3 Liter", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            fuelType == null -> {
                Toast.makeText(this, "Anda harus memilih jenis bahan bakar", Toast.LENGTH_SHORT)
                    .show()
                return
            }
        }


        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()

        getUserInformation()

        val orderId = System.currentTimeMillis().toString()
        val df = SimpleDateFormat("dd-MMM-yyyy, HH:mm:ss")
        val formattedDate: String = df.format(Date())
        priceTotal = priceTotal?.times(liter.toInt())

        Timer().schedule(2000) {
            val order = mapOf(
                "orderId" to orderId,
                "userId" to myUid,
                "username" to name,
                "provinsi" to provinsi,
                "kabupaten" to kabupaten,
                "kecamatan" to kecamatan,
                "kelurahan" to kelurahan,
                "address" to address,
                "orderType" to "BeeFuel",
                "option" to fuelType,
                "date" to formattedDate,
                "qty" to liter.toInt(),
                "status" to "Menunggu",
                "priceTotal" to priceTotal,
                "driverId" to "",
                "driverName" to "",
                "driverNumber" to "",
                "paymentProof" to "",
                "driverImage" to "",
            )


            Timer().schedule(2000) {

                FirebaseFirestore
                    .getInstance()
                    .collection("order")
                    .document(orderId)
                    .set(order)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            mProgressDialog.dismiss()
                            showSuccessDialog()
                        } else {
                            mProgressDialog.dismiss()
                            showFailureDialog()
                        }
                    }
            }
        }


    }

    private fun getUserInformation() {
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(myUid)
            .get()
            .addOnSuccessListener {
                name = it.data?.get("username").toString()
                provinsi = it.data?.get("locProvinsi").toString()
                kabupaten = it.data?.get("locKabupaten").toString()
                kecamatan = it.data?.get("locKecamatan").toString()
                kelurahan = it.data?.get("locKelurahan").toString()
            }
    }


    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Melakukan Order")
            .setMessage("Terdapat kesalahan ketika ingin melakukan order, silahkan periksa koneksi internet anda, dan coba lagi nanti")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Berhasil Melakukan Order")
            .setMessage("Sukses, silahkan cek lebih lanjut pada menu orderan\n\nTerima kasih.")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, i ->
                dialogInterface.dismiss()
                onBackPressed()
            }
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}