package com.project.beeapp.ui.homepage.ui.home.admin_daerah

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.api.RetrofitClient
import com.project.beeapp.api.model.ResponseKecamatan
import com.project.beeapp.api.model.ResponseKelurahan
import com.project.beeapp.api.model.ResponseKota
import com.project.beeapp.api.model.ResponseProvinsi
import com.project.beeapp.databinding.ActivityAdminDaerahDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDaerahDetailActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener  {

    private var binding: ActivityAdminDaerahDetailBinding? = null
    private lateinit var model: AdminModel
    private var isSetLocationTaskVisible = false
    private val taskList = ArrayList<String>()

    private var listIdProv = ArrayList<Int>()
    private var listNameProv = ArrayList<String>()
    private var listIdKota = ArrayList<Int>()
    private var listNameKota = ArrayList<String>()
    private var listIdKec = ArrayList<Int>()
    private var listNameKec = ArrayList<String>()
    private var listIdKel = ArrayList<Int>()
    private var listNameKel = ArrayList<String>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDaerahDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        showProvinsi()


        model = intent.getParcelableExtra<AdminModel>(EXTRA_DATA) as AdminModel
        Glide.with(this)
            .load(model.image)
            .into(binding!!.image)

        binding?.fullname?.text = "Nama Lengkap: ${model.fullname}"
        binding?.username?.text = "Username: ${model.username}"
        binding?.email?.text = "Email: ${model.email}"
        binding?.phone?.text = "No.Handphone: ${model.phone}"
        model.locationTask?.distinct()
        binding?.locationTask?.text = "Bertugas di kecamatan: ${model.locationTask?.joinToString(", ")}"


        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.setDataKecamatan?.setOnClickListener {
            if(!isSetLocationTaskVisible) {
                binding?.setLocationTask?.visibility = View.VISIBLE
                isSetLocationTaskVisible = true
            } else {
                binding?.setLocationTask?.visibility = View.GONE
                isSetLocationTaskVisible = false
            }
        }


        binding?.tambahakanKecamatan?.setOnClickListener {
            val kecamatan = binding?.kecamatan?.selectedItem.toString()

            taskList.add(kecamatan)
            taskList.distinct()

            binding?.locationTaskPlan?.text = taskList.joinToString(", ")
        }


        binding?.registerBtn?.setOnClickListener {
            formValidation()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun formValidation() {
        if (taskList.size == 0) {
            Toast.makeText(this, "Maaf, minimal admin mengatur 1 kecamatan", Toast.LENGTH_SHORT)
                .show()
        } else {
            binding?.progressBar?.visibility = View.VISIBLE

            model.uid?.let {
                FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .document(it)
                    .update("locationTask", taskList)
                    .addOnCompleteListener {
                        taskList.distinct()
                        binding?.locationTask?.text = "Bertugas di kecamatan: ${taskList.joinToString(", ")}"
                        binding?.progressBar?.visibility = View.GONE
                        Toast.makeText(this, "Berhasil memperbarui lokasi kecamatan admin", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun showProvinsi() {
        RetrofitClient.instance.getProvinsi().enqueue(object : Callback<ResponseProvinsi> {
            override fun onResponse(
                call: Call<ResponseProvinsi>,
                response: Response<ResponseProvinsi>
            ) {


                val listResponse = response.body()?.provinsi
                listResponse?.forEach {
                    listIdProv.add(it.id)
                    listNameProv.add(it.nama)
                }

                binding?.provinsi?.onItemSelectedListener = this@AdminDaerahDetailActivity
                val adapter = ArrayAdapter(
                    this@AdminDaerahDetailActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    listNameProv
                )

                binding?.provinsi?.adapter = adapter
            }

            override fun onFailure(call: Call<ResponseProvinsi>, t: Throwable) {
                Toast.makeText(this@AdminDaerahDetailActivity, "${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }


    private fun showKota(idProv: Int) {
        RetrofitClient.instance.getKota(idProv).enqueue(object : Callback<ResponseKota> {
            override fun onResponse(call: Call<ResponseKota>, response: Response<ResponseKota>) {

                val listResponse = response.body()?.kotaKabupaten

                listIdKota.clear()
                listNameKota.clear()
                listResponse?.forEach {
                    listIdKota.add(it.id)
                    listNameKota.add(it.nama)
                }

                binding?.kota?.onItemSelectedListener =this@AdminDaerahDetailActivity
                val adapter = ArrayAdapter(
                    this@AdminDaerahDetailActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    listNameKota
                )
                binding?.kota?.adapter = adapter

            }

            override fun onFailure(call: Call<ResponseKota>, t: Throwable) {
            }

        })
    }


    private fun showKecamatan(idKota: Int) {
        RetrofitClient.instance.getKecamatan(idKota).enqueue(object : Callback<ResponseKecamatan> {
            override fun onResponse(
                call: Call<ResponseKecamatan>,
                response: Response<ResponseKecamatan>
            ) {
                val listResponse = response.body()?.kecamatan

                listIdKec.clear()
                listNameKec.clear()
                listResponse?.forEach {
                    listIdKec.add(it.id)
                    listNameKec.add(it.nama)
                }

                binding?.kecamatan?.onItemSelectedListener = this@AdminDaerahDetailActivity
                val adapter = ArrayAdapter(
                    this@AdminDaerahDetailActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    listNameKec,
                )

                binding?.kecamatan?.adapter = adapter

            }

            override fun onFailure(call: Call<ResponseKecamatan>, t: Throwable) {
            }

        })
    }


    private fun showKelurahan(idKecamatan: Int) {
        RetrofitClient.instance.getKelurahan(idKecamatan)
            .enqueue(object : Callback<ResponseKelurahan> {
                override fun onResponse(
                    call: Call<ResponseKelurahan>,
                    response: Response<ResponseKelurahan>
                ) {
                    val listResponse = response.body()?.kelurahan

                    listIdKel.clear()
                    listNameKel.clear()
                    listResponse?.forEach {
                        listIdKel.add(it.id)
                        listNameKel.add(it.nama)
                    }

                    binding?.kelurahan?.onItemSelectedListener = this@AdminDaerahDetailActivity
                    val adapter = ArrayAdapter(
                        this@AdminDaerahDetailActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        listNameKel,
                    )

                    binding?.kelurahan?.adapter = adapter

                }

                override fun onFailure(call: Call<ResponseKelurahan>, t: Throwable) {

                }

            })
    }


    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        p0?.getItemAtPosition(p2)
        when (p0?.selectedItem) {
            binding?.provinsi?.selectedItem -> {
                showKota(listIdProv[p2])
            }
            binding?.kota?.selectedItem -> {
                showKecamatan(listIdKota[p2])
            }
            binding?.kecamatan?.selectedItem -> {
                showKelurahan(listIdKec[p2])
            }
        }
    }


    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_DATA = "data"
    }
}