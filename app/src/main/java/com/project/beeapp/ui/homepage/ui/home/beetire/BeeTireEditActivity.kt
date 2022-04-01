package com.project.beeapp.ui.homepage.ui.home.beetire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.databinding.ActivityBeeTireEditBinding
import com.project.beeapp.ui.homepage.HomeActivity

class BeeTireEditActivity : AppCompatActivity() {

    private var binding: ActivityBeeTireEditBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeeTireEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val priceBike = intent.getLongExtra(EXTRA_PRICE_BIKE, 0)
        val priceCar = intent.getLongExtra(EXTRA_PRICE_CAR, 0)

        binding?.bike?.setText(priceBike.toString())
        binding?.car?.setText(priceCar.toString())


        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.saveBtn?.setOnClickListener {
            formValidation()
        }

    }

    private fun formValidation() {
        val priceCar = binding?.car?.text.toString().trim()
        val priceBike = binding?.bike?.text.toString().trim()

        when {
            priceCar.isEmpty() -> {
                Toast.makeText(this, "Harga Tambal ban mobil tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            priceBike.isEmpty() -> {
                Toast.makeText(this, "Harga Tambal ban motor tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            else -> {
                binding?.progressBar?.visibility = View.VISIBLE

                val data = mapOf(
                    "car" to priceCar.toLong(),
                    "bike" to priceBike.toLong()
                )

                FirebaseFirestore
                    .getInstance()
                    .collection("pricing")
                    .document("beeTire")
                    .update(data)
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            binding?.progressBar?.visibility = View.GONE
                            Toast.makeText(this, "Berhasil memperbarui harga", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this, HomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            binding?.progressBar?.visibility = View.GONE
                            Toast.makeText(this, "Gagal memperbarui harga", Toast.LENGTH_SHORT)
                                .show()

                        }
                    }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_PRICE_CAR = "car"
        const val EXTRA_PRICE_BIKE = "bike"
    }
}