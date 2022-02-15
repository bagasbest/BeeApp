package com.project.beeapp.ui.homepage.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.project.beeapp.MainActivity
import com.project.beeapp.R
import com.project.beeapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        Glide.with(requireActivity())
            .load(R.drawable.wash)
            .into(binding.beeWash)

        Glide.with(requireActivity())
            .load(R.drawable.tires)
            .into(binding.beeTire)

        Glide.with(requireActivity())
            .load(R.drawable.fuel)
            .into(binding.roundedImageView)

        Glide.with(requireActivity())
            .load(R.drawable.oil)
            .into(binding.roundedImageView6)

        Glide.with(requireActivity())
            .load(R.drawable.pickup)
            .into(binding.roundedImageView3)

        Glide.with(requireActivity())
            .load(R.drawable.gas_water)
            .into(binding.roundedImageView2)

        Glide.with(requireActivity())
            .load(R.drawable.clean)
            .into(binding.roundedImageView5)

        Glide.with(requireActivity())
            .load(R.drawable.paper)
            .into(binding.roundedImageView4)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.view2.setOnClickListener {
            startActivity(Intent(activity, BeeWashActivity::class.java))
        }

        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            activity?.finish()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}