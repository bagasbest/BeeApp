package com.project.beeapp.ui.homepage.ui.home.beefuel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.databinding.ActivityBeeFuelEditBinding
import com.project.beeapp.ui.homepage.ui.home.beewash.BeeWashEditActivity

class BeeFuelEditActivity : AppCompatActivity() {

    private var binding: ActivityBeeFuelEditBinding? = null
    private var model: BeeFuelModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeeFuelEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_PRICE_FUEL)
        binding?.dex?.setText(model?.dex.toString())
        binding?.dexLite?.setText(model?.dexLite.toString())
        binding?.pertalite?.setText(model?.pertalite.toString())
        binding?.pertamax?.setText(model?.pertamax.toString())
        binding?.pertamaxTurbo?.setText(model?.pertamaxTurbo.toString())
        binding?.premium?.setText(model?.premium.toString())
        binding?.solar?.setText(model?.solar.toString())

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.saveBtn?.setOnClickListener {
            formValidation()
        }
    }

    private fun formValidation() {
        val dex = binding?.dex?.text.toString().trim()
        val dexLite = binding?.dexLite?.text.toString().trim()
        val pertalite = binding?.pertalite?.text.toString().trim()
        val pertamax = binding?.pertamax?.text.toString().trim()
        val pertamaxTurbo = binding?.pertamaxTurbo?.text.toString().trim()
        val premium = binding?.premium?.text.toString().trim()
        val solar = binding?.solar?.text.toString().trim()

        when {
            dex.isEmpty() -> {
                Toast.makeText(this, "Maaf, Harga Dex tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            dexLite.isEmpty() -> {
                Toast.makeText(this, "Maaf, Harga Dex Lite tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            pertalite.isEmpty() -> {
                Toast.makeText(this, "Maaf, Harga Pertalite tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            pertamax.isEmpty() -> {
                Toast.makeText(this, "Maaf, Harga Pertamax tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            pertamaxTurbo.isEmpty() -> {
                Toast.makeText(this, "Maaf, Harga Pertamax Turbo tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            premium.isEmpty() -> {
                Toast.makeText(this, "Maaf, Harga Premium tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            solar.isEmpty() -> {
                Toast.makeText(this, "Maaf, Harga Solar tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            else -> {

                binding?.progressBar?.visibility = View.VISIBLE

                val data = mapOf(
                    "Dex" to dex.toLong(),
                    "Dex Lite" to dexLite.toLong(),
                    "Pertalite" to pertalite.toLong(),
                    "Pertamax" to pertamax.toLong(),
                    "Pertamax Turbo" to pertamaxTurbo.toLong(),
                    "Premium" to premium.toLong(),
                    "Solar" to solar.toLong(),
                )

                FirebaseFirestore
                    .getInstance()
                    .collection("pricing")
                    .document("beeFuel")
                    .set(data)
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            binding?.progressBar?.visibility = View.GONE
                            Toast.makeText(this, "Berhasil memperbarui harga", Toast.LENGTH_SHORT).show()
                            onBackPressed()
                        } else {
                            binding?.progressBar?.visibility = View.GONE
                            Toast.makeText(this, "Gagal memperbarui harga", Toast.LENGTH_SHORT).show()

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
        const val EXTRA_PRICE_FUEL = "fuel"
    }
}