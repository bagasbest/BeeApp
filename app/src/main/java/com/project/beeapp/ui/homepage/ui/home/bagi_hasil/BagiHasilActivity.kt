package com.project.beeapp.ui.homepage.ui.home.bagi_hasil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.databinding.ActivityBagiHasilBinding

class BagiHasilActivity : AppCompatActivity() {

    private var binding: ActivityBagiHasilBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBagiHasilBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        getPercentage()
        
        binding?.save?.setOnClickListener { 
            formValidation()
        }

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getPercentage() {
        FirebaseFirestore
            .getInstance()
            .collection("percentage")
            .document("percentage")
            .get()
            .addOnSuccessListener {
                val percentage = it.data?.get("percentage") as Double
                val ppn = it.data?.get("ppn") as Double
                val biayaAdmin = it.data?.get("biayaAdmin") as Long
                binding?.percentage?.setText(String.format("%.0f", percentage*100))
                binding?.ppn?.setText(String.format("%.0f", ppn*100))
                binding?.biayaAdmin?.setText(biayaAdmin.toString())
            }
    }

    private fun formValidation() {
        val percentage = binding?.percentage?.text.toString()
        val ppn = binding?.ppn?.text.toString()
        val biayaAdmin = binding?.biayaAdmin?.text.toString()


        if(percentage.toInt() < 1 || percentage.toInt() > 100) {
            Toast.makeText(this, "Maaf, persentase hanya 1 - 100% saja", Toast.LENGTH_SHORT).show()
        } else if(ppn.toInt() < 1 || ppn.toInt() > 100) {
            Toast.makeText(this, "Maaf, persentase hanya 1 - 100% saja", Toast.LENGTH_SHORT).show()
        } else if(biayaAdmin.isEmpty() || biayaAdmin.toInt() < 1) {
            Toast.makeText(this, "Maaf, biaya admin tidak boleh kosong atau negatif", Toast.LENGTH_SHORT).show()
        } else {
            binding?.progressBar?.visibility = View.VISIBLE

            val percent = mapOf(
                "percentage" to percentage.toDouble() / 100.0,
                "ppn" to ppn.toDouble() / 100.0,
                "biayaAdmin" to biayaAdmin.toLong()
            )

            FirebaseFirestore
                .getInstance()
                .collection("percentage")
                .document("percentage")
                .set(percent)
                .addOnCompleteListener {
                    binding?.progressBar?.visibility = View.GONE
                    Toast.makeText(this, "Berhasil memperbarui bagi hasil", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}