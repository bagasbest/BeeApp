package com.project.beeapp.ui.homepage.ui.home.promotion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.beeapp.databinding.ActivityPromotionBinding

class PromotionActivity : AppCompatActivity() {

    private var binding :ActivityPromotionBinding? = null
    private var promotionList = ArrayList<PromotionModel>()
    private lateinit var adapter: PromotionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPromotionBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        promotionList = intent.getParcelableArrayListExtra(EXTRA_PROMOTION)!!


        initRecyclerView()

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding?.rvPromotion?.layoutManager = layoutManager
        adapter = PromotionAdapter()
        binding?.rvPromotion?.adapter = adapter

        adapter.setData(promotionList)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_PROMOTION = "promotion"
    }
}