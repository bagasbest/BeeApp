package com.project.beeapp.ui.homepage.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.beeapp.databinding.ActivityAdminBinding
import com.project.beeapp.ui.homepage.ui.home.verify_driver.VerifyDriverActivity

class AdminActivity : AppCompatActivity() {

    private var binding: ActivityAdminBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.mitraBtn?.setOnClickListener {
            startActivity(Intent(this, VerifyDriverActivity::class.java))
        }

        binding?.akumulasiBtn?.setOnClickListener {
            startActivity(Intent(this, VerifyDriverActivity::class.java))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}