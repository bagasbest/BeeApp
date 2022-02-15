package com.project.beeapp.ui.homepage.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.R
import com.project.beeapp.databinding.ActivityBeeWashEditBinding

class BeeWashEditActivity : AppCompatActivity() {

    private var binding: ActivityBeeWashEditBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeeWashEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.saveBtn?.setOnClickListener {
            formValidation()
        }
    }

    private fun formValidation() {
        val car = binding?.priceCar?.text.toString().trim()
        val bike = binding?.priceBike?.text.toString().trim()

        if(car.isEmpty()) {
            Toast.makeText(this, "Harga cuci mobil tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        } else if (bike.isEmpty()) {
            Toast.makeText(this, "Harga cuci mobil tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val data = mapOf(
            "car" to car.toLong(),
            "bike" to bike.toLong(),
        )

        FirebaseFirestore
            .getInstance()
            .collection("pricing")
            .document("pricing")
            .update(data)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    Toast.makeText(this, "Berhasil menyimpan harga", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                } else {
                    Toast.makeText(this, "Gagal menyimpan harga", Toast.LENGTH_SHORT).show()

                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}