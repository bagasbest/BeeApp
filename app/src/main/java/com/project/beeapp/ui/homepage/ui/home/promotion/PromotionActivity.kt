package com.project.beeapp.ui.homepage.ui.home.promotion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.databinding.ActivityPromotionBinding
import com.project.beeapp.ui.homepage.HomeActivity

class PromotionActivity : AppCompatActivity() {

    private var binding :ActivityPromotionBinding? = null
    private var promotionList = ArrayList<PromotionModel>()
    private lateinit var adapter: PromotionAdapter
    private var headerText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPromotionBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        promotionList = intent.getParcelableArrayListExtra(EXTRA_PROMOTION)!!
        headerText = intent.getStringExtra(HEADER)

        initRecyclerView()

        binding?.backButton?.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        binding?.save?.setOnClickListener {
            formValidation()
        }

    }

    private fun formValidation() {
        val header = binding?.header?.text.toString().trim()

        if(header.isEmpty()) {
            Toast.makeText(this, "Header tidak boleh kosong", Toast.LENGTH_SHORT).show()
        } else {
            FirebaseFirestore
                .getInstance()
                .collection("image_slider")
                .document(headerText!!)
                .update("text", header)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this, "Berhasil memperbarui header", Toast.LENGTH_SHORT).show()
                    } else{
                        Toast.makeText(this, "Gagal memperbarui header", Toast.LENGTH_SHORT).show()
                    }
                }
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
        const val HEADER = "header"
    }
}