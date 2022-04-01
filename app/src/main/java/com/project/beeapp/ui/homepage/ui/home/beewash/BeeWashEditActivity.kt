package com.project.beeapp.ui.homepage.ui.home.beewash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.MainActivity
import com.project.beeapp.databinding.ActivityBeeWashEditBinding
import com.project.beeapp.ui.homepage.HomeActivity
import com.project.beeapp.ui.homepage.ui.home.HomeFragment

class BeeWashEditActivity : AppCompatActivity() {

    private var binding: ActivityBeeWashEditBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeeWashEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        getPriceFromDatabase()

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.saveBtn?.setOnClickListener {
            formValidation()
        }
    }

    private fun getPriceFromDatabase() {
        FirebaseFirestore
            .getInstance()
            .collection("pricing")
            .document("beeWash")
            .get()
            .addOnSuccessListener {
                if(it.exists()) {
                    val smallCar = it.data?.get("smallCar") as Long
                    val mediumCar = it.data?.get("mediumCar") as Long
                    val largeCar = it.data?.get("largeCar") as Long
                    val smallBike = it.data?.get("smallBike") as Long
                    val mediumBike = it.data?.get("mediumBike") as Long
                    val largeBike = it.data?.get("largeBike") as Long


                    binding?.miniCar?.setText(smallCar.toString())
                    binding?.mediumCar?.setText(mediumCar.toString())
                    binding?.largeCar?.setText(largeCar.toString())
                    binding?.miniBike?.setText(smallBike.toString())
                    binding?.mediumBike?.setText(mediumBike.toString())
                    binding?.largeBike?.setText(largeBike.toString())

                }
            }
    }

    private fun formValidation() {
        val miniCar = binding?.miniCar?.text.toString().trim()
        val mediumCar = binding?.mediumCar?.text.toString().trim()
        val largeCar = binding?.largeCar?.text.toString().trim()
        val miniBike= binding?.miniBike?.text.toString().trim()
        val mediumBike = binding?.mediumBike?.text.toString().trim()
        val largeBike = binding?.largeBike?.text.toString().trim()

        when {
            miniCar.isEmpty() -> {
                Toast.makeText(this, "Harga cuci mobil bertipe kecil tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            mediumCar.isEmpty() -> {
                Toast.makeText(this, "Harga cuci mobil bertipe sedang tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            largeCar.isEmpty() -> {
                Toast.makeText(this, "Harga cuci mobil bertipe besar tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            miniBike.isEmpty() -> {
                Toast.makeText(this, "Harga cuci motor bertipe kecil tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            mediumBike.isEmpty() -> {
                Toast.makeText(this, "Harga cuci motor bertipe sedang tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            largeBike.isEmpty() -> {
                Toast.makeText(this, "Harga cuci motor bertipe besar tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }

            else -> {
                binding?.progressBar?.visibility = View.VISIBLE

                val data = mapOf(
                    "smallCar" to miniCar.toLong(),
                    "mediumCar" to mediumCar.toLong(),
                    "largeCar" to largeCar.toLong(),
                    "smallBike" to miniBike.toLong(),
                    "mediumBike" to mediumBike.toLong(),
                    "largeBike" to largeBike.toLong(),
                )

                FirebaseFirestore
                    .getInstance()
                    .collection("pricing")
                    .document("beeWash")
                    .update(data)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
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
}