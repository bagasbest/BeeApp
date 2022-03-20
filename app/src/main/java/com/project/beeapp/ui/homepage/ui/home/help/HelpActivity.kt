package com.project.beeapp.ui.homepage.ui.home.help

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {

    private var binding: ActivityHelpBinding? = null
    private var description: String? = null

    override fun onResume() {
        super.onResume()
        getRole()
        getHelpDescription()
    }

    private fun getRole() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                if("" + it.data?.get("role") == "admin") {
                    binding?.edit?.visibility = View.VISIBLE
                }
            }

    }

    private fun getHelpDescription() {
        FirebaseFirestore
            .getInstance()
            .collection("help")
            .document("description")
            .get()
            .addOnSuccessListener {
                description = "" + it.data?.get("description")
                binding?.description?.text = description
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        binding?.edit?.setOnClickListener {
            val intent = Intent(this, HelpEditActivity::class.java)
            intent.putExtra(HelpEditActivity.EXTRA_DESCRIPTION, description)
            startActivity(intent)
        }

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}