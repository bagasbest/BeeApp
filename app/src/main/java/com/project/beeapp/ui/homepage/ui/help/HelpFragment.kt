package com.project.beeapp.ui.homepage.ui.help

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.R
import com.project.beeapp.databinding.FragmentHelpBinding


class HelpFragment : Fragment() {

    private var binding : FragmentHelpBinding? = null
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHelpBinding.inflate(inflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.edit?.setOnClickListener {
            val intent = Intent(activity, HelpEditActivity::class.java)
            intent.putExtra(HelpEditActivity.EXTRA_DESCRIPTION, description)

            Glide.with(this)

            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}