package com.project.beeapp.ui.homepage.ui.home.rekening

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.databinding.ActivityRekeningBinding

class RekeningActivity : AppCompatActivity() {

    private var binding: ActivityRekeningBinding? = null
    private lateinit var adapter: RekeningAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRekeningBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initRecyclerView()
        initViewModel()

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.refresh?.setOnClickListener {
            binding?.addOrEdit?.text = "Tambahkan Rekening"
            binding?.bankName?.setText("")
            binding?.recNumber?.setText("")
            binding?.recName?.setText("")
        }


        binding?.save?.setOnClickListener {
            val bankName = binding?.bankName?.text.toString().trim()
            val recName = binding?.recName?.text.toString().trim()
            val recNumber = binding?.recNumber?.text.toString().trim()

            when {
                bankName.isEmpty() -> {
                    Toast.makeText(this, "Maaf, Nama Bank tidak boleh kosong", Toast.LENGTH_SHORT)
                        .show()
                }
                recName.isEmpty() -> {
                    Toast.makeText(
                        this,
                        "Maaf, Nama Pemilik Rekening tidak boleh kosong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                recNumber.isEmpty() -> {
                    Toast.makeText(
                        this,
                        "Maaf, Nomor Rekening tidak boleh kosong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val mProgressDialog = ProgressDialog(this)
                    mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
                    mProgressDialog.setCanceledOnTouchOutside(false)
                    mProgressDialog.show()

                    if (binding?.addOrEdit?.text == "Edit Rekening") {
                        val data = mapOf(
                            "bankName" to bankName,
                            "recName" to recName,
                            "recNumber" to recNumber
                        )

                        FirebaseFirestore
                            .getInstance()
                            .collection("payment")
                            .document(adapter.rekeningUid)
                            .update(data)
                            .addOnCompleteListener {
                                initRecyclerView()
                                initViewModel()
                                Toast.makeText(
                                    this,
                                    "Berhasil memperbarui rekening",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }


                    } else {
                        val uid = System.currentTimeMillis().toString()
                        val data = mapOf(
                            "bankName" to bankName,
                            "recName" to recName,
                            "recNumber" to recNumber,
                            "uid" to uid
                        )

                        FirebaseFirestore
                            .getInstance()
                            .collection("payment")
                            .document(uid)
                            .set(data)
                            .addOnCompleteListener {
                                initRecyclerView()
                                initViewModel()
                                Toast.makeText(
                                    this,
                                    "Berhasil menambahkan rekening pembayaran",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                    }
                }
            }
        }

    }

    private fun initRecyclerView() {
        binding?.rvRekening?.layoutManager = LinearLayoutManager(this)
        adapter = RekeningAdapter(
            binding?.bankName,
            binding?.recName,
            binding?.recNumber,
            binding?.addOrEdit
        )
        binding?.rvRekening?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[RekeningViewModel::class.java]


        binding?.progressBar?.visibility = View.VISIBLE

        viewModel.setListRekening()
        viewModel.getRekeningList().observe(this) { rekeningList ->
            if (rekeningList.size > 0) {
                adapter.setData(rekeningList)
                binding?.noData?.visibility = View.GONE
            } else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding!!.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}