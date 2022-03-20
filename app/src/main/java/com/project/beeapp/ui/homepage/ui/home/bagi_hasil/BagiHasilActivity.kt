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
                binding?.percentage?.setText(String.format("%.0f", percentage*100))
            }
    }

    private fun formValidation() {
        val percentage = binding?.percentage?.text.toString()
        if(percentage.toInt() in 1..100) {

            binding?.progressBar?.visibility = View.VISIBLE

            val percent = mapOf(
                "percentage" to percentage.toDouble() / 100.0
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

        } else {
            Toast.makeText(this, "Maaf, persentase hanya 1 - 100% saja", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}