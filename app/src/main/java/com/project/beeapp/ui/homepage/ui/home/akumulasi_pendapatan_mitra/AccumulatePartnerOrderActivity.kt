package com.project.beeapp.ui.homepage.ui.home.akumulasi_pendapatan_mitra

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.beeapp.databinding.ActivityAccumulatePartnerOrderBinding

class AccumulatePartnerOrderActivity : AppCompatActivity() {

    private var binding: ActivityAccumulatePartnerOrderBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccumulatePartnerOrderBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }
}