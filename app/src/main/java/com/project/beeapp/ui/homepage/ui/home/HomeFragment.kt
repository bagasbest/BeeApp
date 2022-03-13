package com.project.beeapp.ui.homepage.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.MainActivity
import com.project.beeapp.R
import com.project.beeapp.databinding.FragmentHomeBinding
import com.project.beeapp.ui.homepage.ui.home.income.IncomeAdapter
import com.project.beeapp.ui.homepage.ui.home.income.IncomeModel
import com.project.beeapp.ui.homepage.ui.home.income.IncomeViewModel
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null
    private lateinit var incomeAdapter: IncomeAdapter
    private val nominalCurrency: NumberFormat = DecimalFormat("#,###")
    private val user = FirebaseAuth.getInstance().currentUser

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        checkRole()
    }

    @SuppressLint("SetTextI18n")
    private fun checkRole() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                when {
                    "" + it.data?.get("role") == "admin" -> {
                        binding.textView.text = "Beranda BeeFlo"
                        binding.verifyDriver.visibility = View.VISIBLE
                        binding.userOrAdminRole.visibility = View.VISIBLE
                    }
                    "" + it.data?.get("role") == "user" -> {
                        binding.textView.text = "Beranda BeeFlo"
                        binding.userOrAdminRole.visibility = View.VISIBLE
                    }
                    "" + it.data?.get("role") == "driver" -> {
                        binding.textView.text = "Pendapatan Saya"
                        binding.driverRole.visibility = View.VISIBLE

                        initRecyclerView()
                        initViewModel()



                    }
                }
            }
    }

    private fun initRecyclerView() {
        binding.rvIncome.layoutManager = LinearLayoutManager(activity)
        incomeAdapter = IncomeAdapter()
        binding.rvIncome.adapter = incomeAdapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[IncomeViewModel::class.java]
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        binding.progressBarDriver.visibility = View.VISIBLE
        viewModel.setListIncome(uid)
        viewModel.getIncome().observe(this) { income ->
            if (income.size > 0) {
                incomeAdapter.setData(income)
                binding.noData.visibility = View.GONE

                getTotalIncome(income)
                getMonthlyIncome()
                getDailyIncome()
            } else {
                binding.noData.visibility = View.VISIBLE
            }
            binding.progressBarDriver.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun getDailyIncome() {
        val df = SimpleDateFormat("dd-MMM-yyyy")
        val currentDay: String = df.format(Date())


        FirebaseFirestore
            .getInstance()
            .collection("income")
            .whereEqualTo("partnerId", user!!.uid)
            .whereEqualTo("date", currentDay)
            .get()
            .addOnSuccessListener { documents ->
                var dailyIncome = 0L
                for(document in documents) {
                    dailyIncome += document.data["income"] as Long
                }

                binding.daily.text = "Hari ini: Rp.${nominalCurrency.format(dailyIncome)}"
            }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun getMonthlyIncome() {
        val df = SimpleDateFormat("MM")
        val currentMonth: String = df.format(Date())


        FirebaseFirestore
            .getInstance()
            .collection("income")
            .whereEqualTo("partnerId", user!!.uid)
            .whereEqualTo("month", currentMonth)
            .get()
            .addOnSuccessListener { documents ->
                var monthlyIncome = 0L
                for(document in documents) {
                    monthlyIncome += document.data["income"] as Long
                }

                binding.monthly.text = "Bulan ini: Rp.${nominalCurrency.format(monthlyIncome)}"
            }
    }

    @SuppressLint("SetTextI18n")
    private fun getTotalIncome(income: ArrayList<IncomeModel>) {
        var incomeTotal = 0L
        for(i in income.indices) {
            incomeTotal += income[i].income!!
        }

        binding.total.text = "Total: Rp.${nominalCurrency.format(incomeTotal)}"
    }

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

        binding.verifyDriver.setOnClickListener {
            startActivity(Intent(activity, AdminActivity::class.java))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}