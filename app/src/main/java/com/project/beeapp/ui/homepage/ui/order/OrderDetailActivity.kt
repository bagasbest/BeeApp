package com.project.beeapp.ui.homepage.ui.order

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.R
import com.project.beeapp.databinding.ActivityOrderDetailBinding
import com.project.beeapp.notification.NotificationData
import com.project.beeapp.notification.PushNotification
import com.project.beeapp.notification.RetrofitInstance
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailActivity : AppCompatActivity() {

    private var binding: ActivityOrderDetailBinding? = null
    private var model: OrderModel? = null
    private var role: String? = null
    private var percentage: Double? = 0.0
    private var ppn: Double? = 0.0
    private var biayaAdmin: Long? = 0L
    private val uid = FirebaseAuth.getInstance().currentUser!!.uid
    private var notificationUid: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra<OrderModel>(EXTRA_ORDER) as OrderModel
        val nominalCurrency: NumberFormat = DecimalFormat("#,###")

        checkRole()


        if(model?.paymentProof != "") {
            binding?.constraintLayout2?.visibility = View.VISIBLE
            Glide.with(this)
                .load(model?.paymentProof)
                .into(binding!!.paymentProof)

        }

        binding?.orderId?.text = "INV-${model?.orderId}"
        binding?.provinsi?.text = model?.provinsi
        binding?.kabupaten?.text = model?.kabupaten
        binding?.kecamatan?.text = model?.kecamatan
        binding?.kelurahan?.text = model?.kelurahan
        binding?.orderType?.text = "x${model?.qty}      ${model?.orderType} ${model?.option}"
        binding?.status?.text = model?.status
        binding?.address?.text = model?.address
        binding?.priceTotal?.text = "Rp.${nominalCurrency.format(model?.priceTotal)}"


        when (model?.status) {
            "Order Diterima" -> {
                binding?.bgStatus?.backgroundTintList =
                    ContextCompat.getColorStateList(this, android.R.color.holo_orange_dark)
                binding?.noData?.visibility = View.GONE
            }
            "Sudah Bayar" -> {
                binding?.bgStatus?.backgroundTintList =
                    ContextCompat.getColorStateList(this, android.R.color.holo_green_dark)
                binding?.phoneCall?.visibility = View.GONE
            }
            "Selesai" -> {
                binding?.bgStatus?.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.purple_500)
                binding?.noData?.visibility = View.GONE
            }
            "Cash" -> {
                binding?.bgStatus?.backgroundTintList =
                    ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
            else -> {
                binding?.bgStatus?.backgroundTintList =
                    ContextCompat.getColorStateList(this, android.R.color.darker_gray)
                binding?.noData?.text = "Menunggu Verifikasi Admin"
            }
        }



        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }


        binding?.orderBtn?.setOnClickListener {
            if (binding?.orderBtn?.text == "Batalkan Orderan") {
                AlertDialog.Builder(this)
                    .setTitle("Konfirmasi Batalkan Orderan")
                    .setMessage("Apa kamu yakin ingin membatalkan orderan ini ?")
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setPositiveButton("YA") { dialogInterface, _ ->
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
                    .setPositiveButton("YA") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        confirmOrderAsDriver()
                        setStatusDriverWork(true)
                    }
                    .setNegativeButton("TIDAK", null)
                    .show()
            }
        }


        binding?.phoneCall?.setOnClickListener {
            if (model?.status == "Order Diterima" && model?.userId == uid) {
                val intent =
                    Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", model?.driverNumber, null))
                startActivity(intent)
            } else {
                val intent =
                    Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", model?.userNumber, null))
                startActivity(intent)
            }
        }

        binding?.acc?.setOnClickListener {
            if (model?.status == "Order Diterima") {
                showAlertDialogFinishOrder()
            }
            else if (model?.status != "Cash" || model?.status != "Order Diterima" || model?.status != "Selesai") {
                accPaymentDialog()
            }
        }

        binding?.decline?.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Batalkan Orderan")
                .setMessage("Apa kamu yakin ingin membatalkan orderan ini ?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YA") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    sendNotificationToUserDecline()
                    getToken("decline", "user")
                    cancelOrder()
                }
                .setNegativeButton("TIDAK", null)
                .show()
        }

    }

    private fun showAlertDialogFinishOrder() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Menyelesaikan Orderan")
            .setMessage("Apa anda yakin ingin menyelesaikan orderan ini ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                dialogInterface.dismiss()
                finishOrderDialog()
                sendNotificationToUserByDriver(model?.driverName!!, "Selesai")
                getToken("finish", "user")
            }
            .setNegativeButton("TIDAK", null)
            .show()
    }

    private fun setStatusDriverWork(status: Boolean) {
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .update("isWork", status)
    }

    private fun accPaymentDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Menerima Pembayaran")
            .setMessage("Apa kamu yakin sudah menerima transfer dari kustomer ini ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                dialogInterface.dismiss()
                sendNotificationToUserAccept()
                getToken("acc", "user")
                accPayment()
            }
            .setNegativeButton("TIDAK") { dialog, _ ->
                dialog.dismiss()
            }
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
                if (it.isSuccessful) {
                    binding?.acc?.visibility = View.GONE
                    binding?.decline?.visibility = View.GONE
                    binding?.noData?.text = "Sudah Diverifikasi Admin"
                    binding?.status?.text = "Sudah Bayar"
                    binding?.bgStatus?.backgroundTintList =
                        ContextCompat.getColorStateList(this, android.R.color.holo_green_dark)

                    showSuccessDialog(
                        "Sukses Menerima Pembayaran",
                        "Status order ini berubah menjadi Sudah Bayar."
                    )
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

        runBlocking {
            launch {
                delay(1000L)
                FirebaseFirestore
                    .getInstance()
                    .collection("order")
                    .document(model?.orderId!!)
                    .update("status", "Selesai")
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            binding?.acc?.visibility = View.GONE
                            binding?.status?.text = "Selesai"
                            binding?.bgStatus?.backgroundTintList =
                                ContextCompat.getColorStateList(this@OrderDetailActivity, R.color.purple_500)

                            val df = SimpleDateFormat("dd-MMM-yyyy")
                            val dateFinish: String = df.format(Date())

                            val df2 = SimpleDateFormat("MM")
                            val month: String = df2.format(Date())


                            val df3 = SimpleDateFormat("yyyy")
                            val year: String = df3.format(Date())

                            val income = biayaAdmin?.let { it1 -> model?.priceTotal?.minus(it1) }
                            val incomeMinusPPN = income?.minus(income.times(ppn!!))
                            val incomeMinusPercentage = incomeMinusPPN?.times(percentage!!)

                            val data = mapOf(
                                "orderId" to model?.orderId,
                                "partnerId" to model?.driverId,
                                "orderType" to model?.orderType,
                                "date" to dateFinish,
                                "dateTimeInMillis" to System.currentTimeMillis(),
                                "income" to incomeMinusPercentage?.toLong(),
                                "month" to month,
                                "year" to year,
                            )

                            FirebaseFirestore
                                .getInstance()
                                .collection("income")
                                .document(model?.orderId!!)
                                .set(data)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        setStatusDriverWork(false)
                                        Handler().postDelayed({
                                            mProgressDialog.dismiss()
                                            showSuccessDialog(
                                                "Sukses Menyelesaikan Orderan",
                                                "Anda memperoleh pendapatan dari orderan ini, silahkan cek navigasi beranda"
                                            )
                                        }, 1000)
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
            getPercentage()
        }
    }

    private fun getPercentage() {
        FirebaseFirestore
            .getInstance()
            .collection("percentage")
            .document("percentage")
            .get()
            .addOnSuccessListener {
                percentage = it.data?.get("percentage") as Double
                ppn = it.data?.get("ppn") as Double
                biayaAdmin= it.data?.get("biayaAdmin") as Long
            }
    }

    private fun confirmOrderAsDriver() {
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()

        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                val name = "" + it.data?.get("fullname")
                val phone = "" + it.data?.get("phone")
                val image = "" + it.data?.get("image")
                sendNotificationToUserByDriver(name, "Mulai")
                getToken("start", "user")

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
                            if (task.isSuccessful) {
                                mProgressDialog.dismiss()
                                Toast.makeText(this, "Anda menerima order ini", Toast.LENGTH_SHORT)
                                    .show()
                                onBackPressed()
                            } else {
                                mProgressDialog.dismiss()
                                Toast.makeText(
                                    this,
                                    "Upps, gagal menerima order ini, silahkan periksa koneksi internet anda",
                                    Toast.LENGTH_SHORT
                                ).show()
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

                if (role == "driver") {
                    if (model?.status == "Cash" || model?.status == "Sudah Bayar") {
                        binding?.orderBtn?.visibility = View.VISIBLE
                        binding?.orderBtn?.text = "Konfirmasi Orderan"
                    } else if (model?.status == "Order Diterima") {
                        binding?.acc?.visibility = View.VISIBLE

                        binding?.constraintLayout?.visibility = View.VISIBLE

                        Glide.with(this)
                            .load(R.drawable.ic_baseline_account_circle_gray)
                            .into(binding!!.image)

                        binding?.driverName?.text = model?.username
                    }
                } else if (role == "user") {
                    if (model?.status != "Order Diterima" || model?.status != "Selesai") {
                        binding?.orderBtn?.visibility = View.VISIBLE
                        binding?.orderBtn?.text = "Batalkan Orderan"

                    } else {
                        binding?.constraintLayout?.visibility = View.VISIBLE

                        Glide.with(this)
                            .load(model?.driverImage)
                            .into(binding!!.image)

                        binding?.driverName?.text = model?.driverName
                    }
                } else if (role == "admin" || role == "adminKecamatan") {
                    if (model?.status != "Cash" || model?.status != "Order Diterima" || model?.status != "Selesai") {
                        if( model?.status != "Sudah Bayar") {
                            binding?.acc?.visibility = View.VISIBLE
                            binding?.decline?.visibility = View.VISIBLE
                        }
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
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding?.orderBtn?.visibility = View.GONE
                        binding?.constraintLayout?.visibility = View.INVISIBLE
                        Toast.makeText(this, "Berhasil membatalkan orderan ini", Toast.LENGTH_SHORT)
                            .show()
                        onBackPressed()
                    }
                }
        }
    }


    private fun sendNotificationToUserAccept() {

        val df = SimpleDateFormat("dd-MMM-yyyy, HH:mm:ss")
        val formattedDate: String = df.format(Date())
        val uid = System.currentTimeMillis().toString()

        val data = mapOf(
            "title" to "Pembayaran Dikonfirmasi Admin",
            "message" to "Selanjutnya mitra BeeFlo akan mengonfirmasi orderan anda, silahkan tunggu",
            "date" to formattedDate,
            "type" to "user",
            "userId" to model?.userId,
            "uid" to uid
        )

        FirebaseFirestore
            .getInstance()
            .collection("notification")
            .document(uid)
            .set(data)
    }

    private fun sendNotificationToUserDecline() {

        val df = SimpleDateFormat("dd-MMM-yyyy, HH:mm:ss")
        val formattedDate: String = df.format(Date())
        val uid = System.currentTimeMillis().toString()

        val data = mapOf(
            "title" to "Pembayaran Ditolak Admin",
            "message" to "Bukti pembayaran anda ditolak admin, karena tidak ada saldo yang masuk ke rekening",
            "date" to formattedDate,
            "type" to "user",
            "userId" to model?.userId,
            "uid" to uid
        )

        FirebaseFirestore
            .getInstance()
            .collection("notification")
            .document(uid)
            .set(data)
    }

    private fun sendNotificationToUserByDriver(name: String, status: String) {
        val df = SimpleDateFormat("dd-MMM-yyyy, HH:mm:ss")
        val formattedDate: String = df.format(Date())
        val uid = System.currentTimeMillis().toString()

        if(status == "Mulai") {
            val data = mapOf(
                "title" to "Orderan Anda Dikonfirmasi Mitra",
                "message" to "$name, mengkonfirmasi orderanmu",
                "date" to formattedDate,
                "type" to "user",
                "userId" to model?.userId,
                "uid" to uid
            )

            FirebaseFirestore
                .getInstance()
                .collection("notification")
                .document(uid)
                .set(data)
        } else{
            val data = mapOf(
                "title" to "Orderan Anda Diselesaikan Mitra",
                "message" to "$name, berhasil menyelesaikan orderanmu",
                "date" to formattedDate,
                "type" to "user",
                "userId" to model?.userId,
                "uid" to uid
            )

            FirebaseFirestore
                .getInstance()
                .collection("notification")
                .document(uid)
                .set(data)
        }
    }




    private fun getToken(status: String, option: String) {

        notificationUid = when (option) {
            "user" -> {
                model?.userId
            }
            "admin" -> {
                uid
            }
            "driver" -> {
                model?.driverId
            }
            else -> {
                uid
            }
        }

        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(notificationUid!!)
            .get()
            .addOnSuccessListener {
                val token = "" + it.data?.get("token")

                when (status) {
                    "start" -> {
                        PushNotification(
                            NotificationData(
                                "Order Telah Dikonfirmasi",
                                "Mitra ${model?.driverName} menerima orderan anda"
                            ),
                            token
                        ).also { pushNotification ->
                            sendNotification(pushNotification)
                        }
                    }
                    "finish" -> {
                        PushNotification(
                            NotificationData(
                                "Order Telah Diselesaikan",
                                "Mitra ${model?.driverName} telah menyelesaikan orderan"
                            ),
                            token
                        ).also { pushNotification ->
                            sendNotification(pushNotification)
                        }
                    }
                    "acc" -> {
                        PushNotification(
                            NotificationData(
                                "Bukti Pembayaran Diterima",
                                "Admin menerima pembayaran atas order ${model?.orderType} anda"
                            ),
                            token
                        ).also { pushNotification ->
                            sendNotification(pushNotification)
                        }
                        sendNotificationFromUserToMitra()
                    }
                    "decline" -> {
                        PushNotification(
                            NotificationData(
                                "Bukti Pembayaran Ditolak",
                                "Admin menolak pembayaran atas order ${model?.orderType} anda"
                            ),
                            token
                        ).also { pushNotification ->
                            sendNotification(pushNotification)
                        }
                    }
                }
            }
    }

    private fun sendNotificationFromUserToMitra() {
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .whereEqualTo("role", "driver")
            .whereEqualTo("locKecamatan", model?.kecamatan)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    val driverToken = "" + document.data["token"]
                    PushNotification(
                        NotificationData(
                            "Ada order baru",
                            "Order ${model?.orderType} menunggu anda"
                        ),
                        driverToken
                    ).also { pushNotification ->
                        sendNotification(pushNotification)
                    }
                }
            }
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.pushNotification(notification)
                runOnUiThread {
                    if (!response.isSuccessful) {
                        Log.e("Error else", response.body().toString())
                        Toast.makeText(this@OrderDetailActivity, "Token kosong, mohon pastikan koneksi internet anda stabil dan coba lagi", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("Error catch", e.toString())
                runOnUiThread {
                    Toast.makeText(this@OrderDetailActivity, "Token kosong, mohon pastikan koneksi internet anda stabil dan coba lagi", Toast.LENGTH_SHORT).show()
                }
            }
        }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_ORDER = "order"
    }
}