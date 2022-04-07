package com.project.beeapp.ui.homepage.ui.help

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.databinding.ActivityHelpEditBinding

class HelpEditActivity : AppCompatActivity() {

    private var binding: ActivityHelpEditBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.description?.setText(intent.getStringExtra(EXTRA_DESCRIPTION))

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }
        
        binding?.save?.setOnClickListener { 
            formValidation()
        }
        
    }

    private fun formValidation() {
        val description = binding?.description?.text.toString()
        
        if(description.isEmpty()) {
            Toast.makeText(this, "Maaf, Bantuan tidak boleh kosong", Toast.LENGTH_SHORT).show()
        } else {
            binding?.progressBar?.visibility = View.VISIBLE
            FirebaseFirestore
                .getInstance()
                .collection("help")
                .document("description")
                .update("description", description)
                .addOnCompleteListener {
                    binding?.progressBar?.visibility = View.GONE
                    Toast.makeText(this, "Berhasil memperbarui bantuan", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_DESCRIPTION = "description"
    }
}