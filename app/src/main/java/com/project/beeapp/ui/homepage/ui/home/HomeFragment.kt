package com.project.beeapp.ui.homepage.ui.home

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.beeapp.MainActivity
import com.project.beeapp.R
import com.project.beeapp.databinding.FragmentHomeBinding
import com.project.beeapp.ui.homepage.ui.home.beefuel.BeeFuelActivity
import com.project.beeapp.ui.homepage.ui.home.beetire.BeeTireActivity
import com.project.beeapp.ui.homepage.ui.home.beewash.BeeWashActivity
import com.project.beeapp.ui.homepage.ui.home.help.HelpActivity
import com.project.beeapp.ui.homepage.ui.home.income.IncomeAdapter
import com.project.beeapp.ui.homepage.ui.home.income.IncomeModel
import com.project.beeapp.ui.homepage.ui.home.income.IncomeViewModel
import com.project.beeapp.ui.homepage.ui.home.promotion.PromotionActivity
import com.project.beeapp.ui.homepage.ui.home.promotion.PromotionModel
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null
    private lateinit var incomeAdapter: IncomeAdapter
    private val nominalCurrency: NumberFormat = DecimalFormat("#,###")
    private val user = FirebaseAuth.getInstance().currentUser
    private var dp: String? = null
    private val REQUEST_FROM_GALLERY = 1001
    private val promotionList = ArrayList<PromotionModel>()

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
                    "" + it.data?.get("role") == "admin" || "" + it.data?.get("role") == "adminKecamatan" ->   {
                        binding.textView.text = "Beranda BeeFlo"
                        binding.verifyDriver.visibility = View.VISIBLE
                        binding.userOrAdminRole.visibility = View.VISIBLE
                        binding.textView35.text = "Status: Admin"
                        binding.addImageSlider.visibility = View.VISIBLE
                        binding.edit.visibility = View.VISIBLE

                        setImageSlider()


                    }
                    "" + it.data?.get("role") == "user" -> {
                        binding.textView.text = "Beranda BeeFlo"
                        binding.userOrAdminRole.visibility = View.VISIBLE
                        binding.textView35.text = "Status: Kustomer"

                        setImageSlider()

                    }
                    "" + it.data?.get("role") == "driver" -> {
                        binding.textView.text = "Pendapatan Saya"
                        binding.driverRole.visibility = View.VISIBLE
                        binding.textView35.text = "Status: Mitra"

                        initRecyclerView()
                        initViewModel("")



                    }
                }
            }
    }

    private fun setImageSlider() {
        val imageList: ArrayList<SlideModel> = ArrayList() // Create image list
        promotionList.clear()

        FirebaseFirestore
            .getInstance()
            .collection("image_slider")
            .get()
            .addOnSuccessListener { documents ->
                if(documents.size() > 0) {
                    for(document in documents) {
                        val model = PromotionModel()
                        val image = "" + document.data["image"]
                        val uid = "" + document.data["uid"]

                        model.image = image
                        model.uid = uid

                        imageList.add(SlideModel(image, ScaleTypes.CENTER_CROP))
                        promotionList.add(model)
                    }
                    binding.sliderImage.setImageList(imageList)
                }
            }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding.rvIncome.layoutManager = layoutManager
        incomeAdapter = IncomeAdapter()
        binding.rvIncome.adapter = incomeAdapter
    }

    private fun initViewModel(filterDate: String) {
        val viewModel = ViewModelProvider(this)[IncomeViewModel::class.java]
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        binding.progressBarDriver.visibility = View.VISIBLE
        if (filterDate == "") {
            viewModel.setListIncome(uid)
        } else {
            viewModel.setListIncomeByFilter(uid, filterDate)
        }
        viewModel.getIncome().observe(viewLifecycleOwner) { income ->
            if (income.size > 0) {
                incomeAdapter.setData(income)
                binding.noData.visibility = View.GONE
                getTotalIncome(income, filterDate)
                if(filterDate == "") {
                    getMonthlyIncome()
                    getDailyIncome()
                }
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
    private fun getTotalIncome(income: ArrayList<IncomeModel>, filterDate: String) {
        var incomeTotal = 0L
        for(i in income.indices) {
            incomeTotal += income[i].income!!
        }

        if(filterDate == ""){
            binding.total.text = "Total: Rp.${nominalCurrency.format(incomeTotal)}"
        } else {
            binding.daily.visibility = View.GONE
            binding.monthly.visibility = View.GONE
            binding.total.text = "Pendapatan Tanggal Tersebut: Rp.${nominalCurrency.format(incomeTotal)}"
        }
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

        Glide.with(requireActivity())
            .load(R.drawable.logo_trans2)
            .into(binding.imageView3)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.view2.setOnClickListener {
            startActivity(Intent(activity, BeeWashActivity::class.java))
        }

        binding.view5.setOnClickListener {
            startActivity(Intent(activity, BeeTireActivity::class.java))
        }

        binding.view3.setOnClickListener {
            startActivity(Intent(activity, BeeFuelActivity::class.java))
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

        binding.view9.setOnClickListener {
            startActivity(Intent(activity, HelpActivity::class.java))
        }

        binding.helpMitra.setOnClickListener {
            startActivity(Intent(activity, HelpActivity::class.java))
        }

        binding.addImageSlider.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_FROM_GALLERY);
        }

        binding.edit.setOnClickListener {
            val intent = Intent(activity, PromotionActivity::class.java)
            intent.putExtra(PromotionActivity.EXTRA_PROMOTION, promotionList)
            startActivity(intent)
        }

        binding.filter.setOnClickListener {
            showCalendar()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun showCalendar() {
        val datePicker: MaterialDatePicker<*> =
            MaterialDatePicker.Builder.datePicker().setTitleText("Filter Pendapatan Pada Tanggal").build()
        datePicker.show(childFragmentManager, datePicker.toString())
        datePicker.addOnPositiveButtonClickListener { selection: Any ->
            val sdf = SimpleDateFormat("dd-MMM-yyyy")
            val format = sdf.format(Date(selection.toString().toLong()))
            binding.filter.text = format

            initRecyclerView()
            initViewModel(format)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_FROM_GALLERY) {
                uploadArticleDp(data?.data)
            }
        }
    }


    /// fungsi untuk mengupload foto kedalam cloud storage
    private fun uploadArticleDp(data: Uri?) {
        val mStorageRef = FirebaseStorage.getInstance().reference
        val mProgressDialog = ProgressDialog(activity)
        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
        val imageFileName = "imageSlider/image_" + System.currentTimeMillis() + ".png"
        mStorageRef.child(imageFileName).putFile(data!!)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        dp = uri.toString()
                        saveImageSliderToDatabase(mProgressDialog)
                    }
                    .addOnFailureListener { e: Exception ->
                        mProgressDialog.dismiss()
                        Toast.makeText(
                            activity,
                            "Gagal mengunggah gambar",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("imageDp: ", e.toString())
                    }
            }
            .addOnFailureListener { e: Exception ->
                mProgressDialog.dismiss()
                Toast.makeText(
                    activity,
                    "Gagal mengunggah gambar",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.d("imageDp: ", e.toString())
            }
    }

    private fun saveImageSliderToDatabase(mProgressDialog: ProgressDialog) {
        val uid = System.currentTimeMillis().toString()
        val data = mapOf(
            "image" to dp,
            "uid" to uid
        )
        FirebaseFirestore
            .getInstance()
            .collection("image_slider")
            .document(uid)
            .set(data)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    mProgressDialog.dismiss()
                    setImageSlider()
                } else {
                    mProgressDialog.dismiss()
                    Toast.makeText(activity, "Gagal menambahkan slider image", Toast.LENGTH_SHORT).show()
                }
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}