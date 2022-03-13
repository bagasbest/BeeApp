package com.project.beeapp.ui.homepage.ui.order

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.beeapp.R
import com.project.beeapp.databinding.ActivityOrderDetailBinding
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailActivity : AppCompatActivity() {

    private var binding: ActivityOrderDetailBinding? = null
    private var model: OrderModel? = null
    private var role: String? = null
    private var dp: String? = null
    private val REQUEST_FROM_GALLERY = 1001

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)



        model = intent.getParcelableExtra<OrderModel>(EXTRA_ORDER) as OrderModel
        val nominalCurrency: NumberFormat = DecimalFormat("#,###")

        checkRole()


        binding?.orderId?.text = "INV-${model?.orderId}"
        binding?.provinsi?.text = model?.provinsi
        binding?.kabupaten?.text = model?.kabupaten
        binding?.kecamatan?.text = model?.kecamatan
        binding?.kelurahan?.text = model?.kelurahan
        binding?.orderType?.text = "x${model?.qty}      ${model?.orderType} ${model?.option}"
        binding?.status?.text = model?.status
        binding?.address?.text = model?.address
        binding?.priceTotal?.text = "Rp.${nominalCurrency.format(model?.priceTotal)}"

        if(model?.paymentProof != "") {
            Glide.with(this)
                .load(model?.paymentProof)
                .into(binding!!.paymentProof)
        }

        when (model?.status) {
            "Menunggu" -> {
                binding?.bgStatus?.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
            "Order Diterima" -> {
                binding?.bgStatus?.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_orange_dark)
            }
            "Belum Bayar" -> {
                binding?.bgStatus?.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_blue_dark)
            }
            "Sudah Bayar" -> {
                binding?.bgStatus?.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_green_dark)
            }
            "Selesai" -> {
                binding?.bgStatus?.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple_500)
            }
        }

        if(model?.status != "Menunggu") {
            binding?.noData?.visibility = View.GONE
            binding?.payment?.visibility = View.VISIBLE
        }


        if(model?.status == "Belum Bayar" || model?.status == "Order Diterima") {
            binding?.imageHint?.visibility = View.VISIBLE
        }




        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        // KLIK TAMBAH GAMBAR
        binding?.imageHint?.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_FROM_GALLERY);
        }


        binding?.orderBtn?.setOnClickListener {
            if(binding?.orderBtn?.text == "Batalkan Orderan") {
                AlertDialog.Builder(this)
                    .setTitle("Konfirmasi Batalkan Orderan")
                    .setMessage("Apa kamu yakin ingin membatalkan orderan ini ?")
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setPositiveButton("YA") { dialogInterface,_ ->
                        dialogInterface.dismiss()
                        cancelOrder()
                    }
                    .setNegativeButton("TIDAK", null)
                    .show()
            } else if (binding?.orderBtn?.text == "Konfirmasi Orderan") {
                AlertDialog.Builder(this)
                    .setTitle("Konfirmasi Orderan")
                    .setMessage("Apa kamu yakin ingin mengongfirmasi orderan ini ?")
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setPositiveButton("YA") { dialogInterface,_ ->
                        dialogInterface.dismiss()
                        confirmOrderAsDriver()
                    }
                    .setNegativeButton("TIDAK", null)
                    .show()
            }
        }


        binding?.phoneCall?.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + model?.driverNumber))
            startActivity(intent)
        }

        binding?.acc?.setOnClickListener {
            if(binding?.status?.text.toString() == "Order Diterima") {
                accPaymentDialog()
            } else if (binding?.status?.text.toString() == "Sudah Bayar"){
                finishOrderDialog()
            }
        }

        binding?.decline?.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Batalkan Orderan")
                .setMessage("Apa kamu yakin ingin membatalkan orderan ini ?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YA") { dialogInterface,_ ->
                    dialogInterface.dismiss()
                    cancelOrder()
                }
                .setNegativeButton("TIDAK", null)
                .show()
        }

    }

    private fun accPaymentDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Menerima Pembayaran")
            .setMessage("Apa kamu yakin sudah menerima transfer dari kustomer ini ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YA") { dialogInterface,_ ->
                dialogInterface.dismiss()
                accPayment()
            }
            .setNegativeButton("TIDAK", null)
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun accPayment() {
        FirebaseFirestore
            .getInstance()
            .collection("order")
            .document(model?.orderId!!)
            .update("status", "Sudah Bayar")
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    binding?.acc?.visibility = View.GONE
                    binding?.decline?.visibility = View.GONE
                    binding?.status?.text = "Sudah Bayar"
                    binding?.bgStatus?.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_green_dark)

                    showSuccessDialog("Sukses Menerima Pembayaran", "Status order ini berubah menjadi Sudah Bayar.")
                } else {
                    showFailureDialog("Gagal Menerima Pembayaran")
                }
            }
    }

    private fun showFailureDialog(title: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage("Mohon periksa koneksi internet anda dan coba lagi nanti")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun finishOrderDialog() {

        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()

        FirebaseFirestore
            .getInstance()
            .collection("order")
            .document(model?.orderId!!)
            .update("status", "Selesai")
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    binding?.acc?.visibility = View.GONE
                    binding?.status?.text = "Selesai"
                    binding?.bgStatus?.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple_500)

                    val df = SimpleDateFormat("dd-MMM-yyyy")
                    val dateFinish: String = df.format(Date())

                    val df2= SimpleDateFormat("MM")
                    val month: String = df2.format(Date())


                    val df3= SimpleDateFormat("yyyy")
                    val year: String = df3.format(Date())

                    val income = model?.priceTotal?.minus((model?.priceTotal?.times(0.2)!!))

                    val data = mapOf(
                        "orderId" to model?.orderId,
                        "partnerId" to model?.driverId,
                        "orderType" to model?.orderType,
                        "date" to dateFinish,
                        "dateTimeInMillis" to System.currentTimeMillis(),
                        "income" to income?.toLong(),
                        "month" to month,
                        "year" to year,
                    )

                    FirebaseFirestore
                        .getInstance()
                        .collection("income")
                        .document(model?.orderId!!)
                        .set(data)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful) {
                                mProgressDialog.dismiss()
                                showSuccessDialog("Sukses Menyelesaikan Orderan", "Anda memperoleh pendapatan dari orderan ini, silahkan cek navigasi beranda")
                            } else {
                                mProgressDialog.dismiss()
                                showFailureDialog("Gagal Menyelesaikan Orderan")
                            }
                        }

                } else {
                    mProgressDialog.dismiss()
                    showFailureDialog("Gagal Menyelesaikan Orderan")
                }
            }
    }

    private fun confirmOrderAsDriver() {
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                val name = "" + it.data?.get("fullname")
                val phone = "" + it.data?.get("phone")
                val image = "" + it.data?.get("image")

                val data = mapOf(
                    "driverId" to uid,
                    "driverImage" to image,
                    "driverName" to name,
                    "driverNumber" to phone,
                    "status" to "Order Diterima"
                )


                model?.orderId?.let { it1 ->
                    FirebaseFirestore
                        .getInstance()
                        .collection("order")
                        .document(it1)
                        .update(data)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful) {
                                mProgressDialog.dismiss()
                                Toast.makeText(this, "Anda menerima order ini", Toast.LENGTH_SHORT).show()
                                onBackPressed()
                            } else {
                                mProgressDialog.dismiss()
                                Toast.makeText(this, "Upps, gagal menerima order ini, silahkan periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
                            }
                        }
                }


            }
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
               role = it.data?.get("role").toString()

                if(role == "driver") {
                    if(model?.status == "Menunggu") {
                        binding?.orderBtn?.visibility = View.VISIBLE
                        binding?.orderBtn?.text = "Konfirmasi Orderan"
                    } else if (model?.status == "Sudah Bayar") {
                        binding?.acc?.visibility = View.VISIBLE
                    }
                } else if(role == "user") {
                    if(model?.status == "Menunggu") {
                        binding?.orderBtn?.visibility = View.VISIBLE
                        binding?.orderBtn?.text = "Batalkan Orderan"
                        binding?.rekening?.visibility = View.VISIBLE
                        binding?.payment?.visibility = View.VISIBLE

                    } else {
                        binding?.constraintLayout?.visibility = View.VISIBLE

                        Glide.with(this)
                            .load(model?.driverImage)
                            .into(binding!!.image)

                        binding?.driverName?.text = model?.driverName
                    }
                } else if (role == "admin") {
                    if(model?.status == "Menunggu") {
                        binding?.orderBtn?.visibility = View.VISIBLE
                        binding?.orderBtn?.text = "Batalkan Orderan"
                        binding?.rekening?.visibility = View.VISIBLE
                        binding?.payment?.visibility = View.VISIBLE

                    } else if(model?.status == "Order Diterima") {
                        binding?.acc?.visibility = View.VISIBLE
                        binding?.decline?.visibility = View.VISIBLE
                    } else {
                        binding?.constraintLayout?.visibility = View.VISIBLE

                        Glide.with(this)
                            .load(model?.driverImage)
                            .into(binding!!.image)

                        binding?.driverName?.text = model?.driverName
                    }
                }
            }
    }

    private fun cancelOrder() {
        model?.orderId?.let {
            FirebaseFirestore
                .getInstance()
                .collection("order")
                .document(it)
                .delete()
                .addOnCompleteListener { task->
                    if(task.isSuccessful) {
                        binding?.orderBtn?.visibility = View.GONE
                        binding?.constraintLayout?.visibility = View.INVISIBLE
                        Toast.makeText(this, "Berhasil membatalkan orderan ini", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_FROM_GALLERY) {
                uploadArticleDp(data?.data)
            }
        }
    }


    /// fungsi untuk mengupload foto kedalam cloud storage
    private fun uploadArticleDp(data: Uri?) {
        val mStorageRef = FirebaseStorage.getInstance().reference
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
        val imageFileName = "paymentProof/image_" + System.currentTimeMillis() + ".png"
        mStorageRef.child(imageFileName).putFile(data!!)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        mProgressDialog.dismiss()
                        dp = uri.toString()
                        savePaymentProofOnDatabase()
                        Glide
                            .with(this)
                            .load(dp)
                            .into(binding!!.paymentProof)
                    }
                    .addOnFailureListener { e: Exception ->
                        mProgressDialog.dismiss()
                        Toast.makeText(
                            this,
                            "Gagal mengunggah gambar",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("imageDp: ", e.toString())
                    }
            }
            .addOnFailureListener { e: Exception ->
                mProgressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Gagal mengunggah gambar",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.d("imageDp: ", e.toString())
            }
    }

    private fun savePaymentProofOnDatabase() {
        FirebaseFirestore
            .getInstance()
            .collection("order")
            .document(model?.orderId!!)
            .update("paymentProof", dp)
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_ORDER = "order"
    }
}