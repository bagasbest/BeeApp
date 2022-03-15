package com.project.beeapp.ui.homepage.ui.home.beetire

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.R
import com.project.beeapp.databinding.ActivityBeeTireBinding
import com.project.beeapp.ui.homepage.ui.home.beewash.BeeWashEditActivity
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

class BeeTireActivity : AppCompatActivity() {

    private var binding: ActivityBeeTireBinding? = null
    private var option: String? = null
    private val myUid = FirebaseAuth.getInstance().currentUser!!.uid
    private var provinsi: String? = null
    private var kabupaten: String? = null
    private var kecamatan: String? = null
    private var kelurahan: String? = null
    private var name: String? = null
    private var priceCar: Long? = 0L
    private var priceBike: Long? = 0L
    private var priceTotal: Long? = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeeTireBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val format: NumberFormat = DecimalFormat("#,###")


        checkRole()
        getPricing()

        binding?.qty?.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(qty: Editable?) {
                if (qty.toString().isEmpty() || option == null) {
                    priceTotal = 0
                    binding?.priceTotal?.text = "Rp.${priceTotal}"
                } else {
                    if (option == "car") {
                        priceTotal = priceCar?.times(qty.toString().toLong())
                        binding?.priceTotal?.text = "Rp.${format.format(priceTotal)}"
                    } else {
                        priceTotal = priceBike?.times(qty.toString().toLong())
                        binding?.priceTotal?.text = "Rp.${format.format(priceTotal)}"
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.view13?.setOnClickListener {
            option = "bike"
            Toast.makeText(this, "Memilih menambal ban motor", Toast.LENGTH_SHORT).show()
            binding?.qty?.setText("")
        }

        binding?.view14?.setOnClickListener {
            option = "car"
            Toast.makeText(this, "Memilih menambal ban mobil", Toast.LENGTH_SHORT).show()
            binding?.qty?.setText("")
        }

        binding?.orderBtn?.setOnClickListener {
            formValidation()
        }

        binding?.edit?.setOnClickListener {
            val intent = Intent (this, BeeWashEditActivity::class.java)
            intent.putExtra(BeeWashEditActivity.EXTRA_CAR, priceCar.toString())
            intent.putExtra(BeeWashEditActivity.EXTRA_BIKE, priceBike.toString())
            intent.putExtra(BeeWashEditActivity.OPTION, "beeTire")
            startActivity(intent)
        }

    }


    private fun getPricing() {
        FirebaseFirestore
            .getInstance()
            .collection("pricing")
            .document("beeTire")
            .get()
            .addOnSuccessListener {
                if(it.exists()) {
                    priceCar = it.data?.get("car") as Long
                    priceBike = it.data?.get("bike") as Long
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
        val qty = binding?.qty?.text.toString().trim()
        when {
            address.isEmpty() -> {
                Toast.makeText(this, "Alamat lengkap tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            qty.isEmpty() -> {
                Toast.makeText(this, "Kuantitas kendaraan tidak boleh kosong", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            option == null -> {
                Toast.makeText(this, "Silahkan pilih ingin mencuci mobil atau motor", Toast.LENGTH_SHORT)
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
                "orderType" to "BeeTire",
                "option" to option,
                "date" to formattedDate,
                "qty" to qty.toInt(),
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