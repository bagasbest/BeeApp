package com.project.beeapp.ui.homepage.ui.home.verify_driver

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.R
import com.project.beeapp.databinding.ActivityVerifyDriverDetailBinding


class VerifyDriverDetailActivity : AppCompatActivity() {

    private var binding: ActivityVerifyDriverDetailBinding? = null
    private var model: VerifyDriverModel? = null
    private var status: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyDriverDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_DRIVER)

        Glide.with(this)
            .load(model?.image)
            .into(binding!!.image)

        binding?.status?.text = "Status: ${model?.status}"

        binding?.email?.text = "Email: ${model?.email}"
        binding?.fullname?.text = "Nama Lengkap: ${model?.fullname}"
        binding?.locKabupaten?.text = "Kabupaten: ${model?.locKabupaten}"
        binding?.locKecamatan?.text = "Kecamatan: ${model?.locKecamatan}"
        binding?.locKelurahan?.text = "Kelurahan: ${model?.locKelurahan}"
        binding?.locProvinsi?.text = "Provinsi: ${model?.locProvinsi}"
        binding?.username?.text = "Username: ${model?.username}"
        binding?.phone?.text = "No.Handphone: ${model?.phone}"
        binding?.npwp?.text = "NPWP: ${model?.npwp}"


        statusChoice()

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }


        binding?.btnConfirm?.setOnClickListener {
            if(status == null) {
                Toast.makeText(this, "Maaf, anda belum memilih status untuk Mitra", Toast.LENGTH_SHORT).show()
            } else {
                model?.uid?.let { it1 ->
                    FirebaseFirestore
                        .getInstance()
                        .collection("users")
                        .document(it1)
                        .update("status", status)
                        .addOnCompleteListener {
                            if(it.isSuccessful) {
                                showSuccessDialog()
                            } else {
                                showFailureDialog()
                            }
                        }
                }
            }
        }

    }

    private fun statusChoice() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.driver_status, android.R.layout.simple_list_item_1
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding!!.statusEt.setAdapter(adapter)
        binding!!.statusEt.setOnItemClickListener { _, _, _, _ ->
            status = binding!!.statusEt.text.toString()
        }
    }

    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal melakukan Verifikasi")
            .setMessage("Silahkan periksa koneksi internet dan coba lagi nanti")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                binding?.progressBar?.visibility = View.GONE
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Berhasil melakukan Verifikasi")
            .setMessage("Berhasil memperbarui status Mitra")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                binding?.progressBar?.visibility = View.GONE
                binding?.status?.text = status
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_DRIVER = "driver"
    }
}