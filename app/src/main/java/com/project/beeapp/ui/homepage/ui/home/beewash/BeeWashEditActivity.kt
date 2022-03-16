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

        val option = intent.getStringExtra(OPTION);
        if(option == "beeTire") {
            binding?.textView5?.text = "Bee Tire Edit Harga"
            binding?.priceCar?.hint = "Harga Tambal Ban Mobil"
            binding?.priceBike?.hint = "Harga Tambal Ban Motor"
        }

        binding?.priceBike?.setText(intent.getStringExtra(EXTRA_BIKE))
        binding?.priceCar?.setText(intent.getStringExtra(EXTRA_CAR))

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

        binding?.progressBar?.visibility = View.VISIBLE

        val data = mapOf(
            "car" to car.toLong(),
            "bike" to bike.toLong(),
        )

        val document = intent.getStringExtra(OPTION)

        FirebaseFirestore
            .getInstance()
            .collection("pricing")
            .document(document!!)
            .set(data)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    binding?.progressBar?.visibility = View.GONE
                    Toast.makeText(this, "Berhasil memperbarui harga", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    binding?.progressBar?.visibility = View.GONE
                    Toast.makeText(this, "Gagal memperbarui harga", Toast.LENGTH_SHORT).show()

                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_CAR = "car"
        const val EXTRA_BIKE = "bike"
        const val OPTION = "option"
    }
}