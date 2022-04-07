package com.project.beeapp.ui.homepage.ui.home.beewash

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.beeapp.R
import com.project.beeapp.api.RetrofitClient
import com.project.beeapp.api.model.ResponseKecamatan
import com.project.beeapp.api.model.ResponseKelurahan
import com.project.beeapp.api.model.ResponseKota
import com.project.beeapp.api.model.ResponseProvinsi
import com.project.beeapp.databinding.ActivityBeeWashBinding
import com.project.beeapp.notification.NotificationData
import com.project.beeapp.notification.PushNotification
import com.project.beeapp.notification.RetrofitInstance
import com.project.beeapp.ui.homepage.ui.home.beefuel.PaymentModel
import com.project.beeapp.utils.SendNotification
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

class BeeWashActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener  {
    private var binding: ActivityBeeWashBinding? = null
    private var option: String? = null
    private val myUid = FirebaseAuth.getInstance().currentUser!!.uid
    private var provinsi: String? = null
    private var kabupaten: String? = null
    private var kecamatan: String? = null
    private var kelurahan: String? = null
    private var name: String? = null
    private var phone: String? = null
    private var priceSmallCar: Long? = 0L
    private var priceMediumCar: Long? = 0L
    private var priceLargeCar: Long? = 0L
    private var priceSmallBike: Long? = 0L
    private var priceMediumBike: Long? = 0L
    private var priceLargeBike: Long? = 0L
    private var priceTotal: Long? = 0L

    private var bankName: String? = null
    private var recName: String? = null
    private var recNumber: String? = null

    private var dp: String? = null
    private val REQUEST_FROM_GALLERY = 1001

    private var listIdProv   = ArrayList<Int>()
    private var listNameProv = ArrayList<String>()
    private var listIdKota   = ArrayList<Int>()
    private var listNameKota = ArrayList<String>()
    private var listIdKec    = ArrayList<Int>()
    private var listNameKec  = ArrayList<String>()
    private var listIdKel    = ArrayList<Int>()
    private var listNameKel  = ArrayList<String>()

    private var locationOption: String? = null
    private var vehicleType: String? = null
    private var notificationUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeeWashBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val format: NumberFormat = DecimalFormat("#,###")


        checkRole()
        getPricing()
        setPaymentChoose()
        setLocationChoose()
        setVehicleType()

        Glide.with(this)
            .load(R.drawable.logo_trans2)
            .into(binding!!.imageView3)


        binding?.qty?.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(qty: Editable?) {
                if (qty.toString().isEmpty() || option == null || vehicleType == null) {
                    priceTotal = 0
                    binding?.priceTotal?.text = "Total Biaya Rp.${priceTotal}"
                } else {
                    if (option == "car") {
                        when (vehicleType) {
                            "Kendaraan Kecil" -> {
                                priceTotal = priceSmallCar?.times(qty.toString().toLong())
                                binding?.priceTotal?.text = "Total Biaya Rp.${format.format(priceTotal)}"
                            }
                            "Kendaraan Sedang" -> {
                                priceTotal = priceMediumCar?.times(qty.toString().toLong())
                                binding?.priceTotal?.text = "Total Biaya Rp.${format.format(priceTotal)}"
                            }
                            else -> {
                                priceTotal = priceLargeCar?.times(qty.toString().toLong())
                                binding?.priceTotal?.text = "Total Biaya Rp.${format.format(priceTotal)}"
                            }
                        }

                    } else {
                        when (vehicleType) {
                            "Kendaraan Kecil" -> {
                                priceTotal = priceSmallBike?.times(qty.toString().toLong())
                                binding?.priceTotal?.text = "Total Biaya Rp.${format.format(priceTotal)}"
                            }
                            "Kendaraan Sedang" -> {
                                priceTotal = priceMediumBike?.times(qty.toString().toLong())
                                binding?.priceTotal?.text = "Total Biaya Rp.${format.format(priceTotal)}"
                            }
                            else -> {
                                priceTotal = priceLargeBike?.times(qty.toString().toLong())
                                binding?.priceTotal?.text = "Total Biaya Rp.${format.format(priceTotal)}"
                            }
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.view13?.setOnClickListener {
            option = "bike"
            binding?.view13?.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple_500)
            binding?.view14?.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple_200)
            Toast.makeText(this, "Memilih mencuci motor", Toast.LENGTH_SHORT).show()
            binding?.qty?.setText("")
        }

        binding?.view14?.setOnClickListener {
            option = "car"
            binding?.view13?.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple_200)
            binding?.view14?.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple_500)
            Toast.makeText(this, "Memilih mencuci mobil", Toast.LENGTH_SHORT).show()
            binding?.qty?.setText("")
        }

        binding?.orderBtn?.setOnClickListener {
            formValidation()
        }

        binding?.edit?.setOnClickListener {
            val intent = Intent (this, BeeWashEditActivity::class.java)
            startActivity(intent)
        }

        binding?.info?.setOnClickListener {
            val btnSubmit: Button
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.popup_info_payment)
            dialog.setCanceledOnTouchOutside(false)
            btnSubmit = dialog.findViewById(R.id.submit)

            btnSubmit.setOnClickListener {
                dialog.dismiss()
            }

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setVehicleType() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.type, android.R.layout.simple_list_item_1
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding?.type?.setAdapter(adapter)
        binding?.type?.setOnItemClickListener { _, _, _, _ ->
            binding?.typeDescriptor?.visibility = View.VISIBLE
            binding?.qty?.setText("")
            vehicleType = binding?.type?.text.toString()

            when (vehicleType) {
                "Kendaraan Kecil" -> {
                    priceTotal = 0
                    binding?.priceTotal?.text = "Total Biaya Rp.${priceTotal}"
                    binding?.typeDescriptor?.text = "Motor: Sejenis Honda Beat, Vario, Mio atau motor gigi seperti Supra, Vega, atau Jupiter\n\nMobil: Sejenis Sedan, Hatchback, atau Station dan mobil bertipe kecil"
                }
                "Kendaraan Sedang" -> {
                    priceTotal = 0
                    binding?.priceTotal?.text = "Total Biaya Rp.${priceTotal}"
                    binding?.typeDescriptor?.text = "Motor: Sejenis Motor NMAX\n\nMobil: Sejenis Minibus Avanza, Inova, Toyota dan sebagainya,"
                }
                else -> {
                    priceTotal = 0
                    binding?.priceTotal?.text = "Total Biaya Rp.${priceTotal}"
                    binding?.typeDescriptor?.text = "Motor: Sejenis Vixion, Kawasaki, Verza, serta motor besar lainnya\n\nMobil: Sejenis Truk atau Bus"

                }
            }

        }
    }

    private fun setLocationChoose() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.location_option, android.R.layout.simple_list_item_1
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding?.locationOption?.setAdapter(adapter)
        binding?.locationOption?.setOnItemClickListener { _, _, _, _ ->
            locationOption = binding?.locationOption?.text.toString()

            if(locationOption == "Lokasi Lain") {
                binding?.linearLayout?.visibility = View.VISIBLE
                binding?.addressNow?.visibility = View.VISIBLE

                showProvinsi()
            } else {
                binding?.addressNow?.visibility = View.VISIBLE
                binding?.linearLayout?.visibility = View.GONE
            }

        }

    }


    private fun setPaymentChoose() {

        val paymentList = ArrayList<PaymentModel>()
        val bankList = ArrayList<String>()

        FirebaseFirestore
            .getInstance()
            .collection("payment")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val model = PaymentModel()
                    model.bankName = "" + document.data["bankName"]
                    model.recName = "" + document.data["recName"]
                    model.recNumber = "" + document.data["recNumber"]
                    model.uid = "" + document.data["uid"]

                    paymentList.add(model)
                    bankList.add(model.bankName!!)
                }


                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    bankList
                )
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                binding?.paymentList?.setAdapter(adapter)
                binding?.paymentList?.setOnItemClickListener { _, _, i, _ ->

                    bankName = paymentList[i].bankName
                    recNumber = paymentList[i].recNumber
                    recName = paymentList[i].recName

                    if(bankName != "Cash") {
                        binding?.rekening?.visibility = View.VISIBLE
                        binding?.payment?.visibility = View.VISIBLE
                    } else {
                        binding?.rekening?.visibility = View.GONE
                        binding?.payment?.visibility = View.GONE
                    }

                    binding?.bankName?.text = bankName
                    binding?.recNumber?.text = recNumber
                    binding?.recName?.text = recName

                }

            }

        // KLIK TAMBAH GAMBAR
        binding?.imageHint?.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_FROM_GALLERY);
        }
    }

    private fun getPricing() {
        FirebaseFirestore
            .getInstance()
            .collection("pricing")
            .document("beeWash")
            .get()
            .addOnSuccessListener {
                if(it.exists()) {
                    priceSmallCar = it.data?.get("smallCar") as Long
                    priceMediumCar = it.data?.get("mediumCar") as Long
                    priceLargeCar = it.data?.get("largeCar") as Long
                    priceSmallBike = it.data?.get("smallBike") as Long
                    priceMediumBike = it.data?.get("mediumBike") as Long
                    priceLargeBike = it.data?.get("largeBike") as Long
                }
            }
    }

    private fun checkRole() {
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(myUid)
            .get()
            .addOnSuccessListener {
                if (it.data?.get("role") == "admin") {
                    binding?.edit?.visibility = View.VISIBLE
                }
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SimpleDateFormat")
    private fun formValidation() {
        val address = binding?.address?.text.toString().trim()
        provinsi = binding?.provinsi?.selectedItem.toString()
        kabupaten = binding?.kota?.selectedItem.toString()
        kecamatan = binding?.kecamatan?.selectedItem.toString()
        kelurahan = binding?.kelurahan?.selectedItem.toString()

        val qty = binding?.qty?.text.toString().trim()
        when {
            qty.isEmpty() -> {
                Toast.makeText(this, "Kuantitas kendaraan tidak boleh kosong", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            option == null -> {
                Toast.makeText(this, "Silahkan pilih ingin mencuci mobil atau motor", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            bankName != "Cash" && dp == null -> {
                Toast.makeText(this, "Anda harus mengunggah bukti pembayaran, sebelum melakukan order", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            locationOption == null -> {
                Toast.makeText(this, "Anda harus memilih lokasi", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            locationOption == "Sesuai Alamat Anda" -> {
              if(address.isEmpty()) {
                  Toast.makeText(this, "Detail lokasi anda tidak boleh kosong", Toast.LENGTH_SHORT)
                      .show()
                  return
              }
            }
            locationOption == "Lokasi Lain" -> {
                if(provinsi == null || kabupaten == null || kecamatan == null || kelurahan == null) {
                    Toast.makeText(this, "Silahkan pilih lokasi anda saat ini", Toast.LENGTH_SHORT)
                        .show()
                    return
                }
                if(address.isEmpty()) {
                    Toast.makeText(this, "Detail lokasi anda tidak boleh kosong", Toast.LENGTH_SHORT)
                        .show()
                    return
                }
            }
        }


        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()

        getUserInformation()


        val orderId = System.currentTimeMillis().toString()
        val df = SimpleDateFormat("dd-MMM-yyyy, HH:mm:ss")
        val formattedDate: String = df.format(Date())


        Timer().schedule(2000) {

            if(bankName == "Cash") {
                dp = ""
                getToken(name!!, "user")
            } else {
                sendNotificationToAdmin(name!!, myUid, bankName)
                getToken(name!!, "admin")
            }


            val order = mapOf(
                "orderId" to orderId,
                "userId" to myUid,
                "username" to name,
                "provinsi" to provinsi,
                "kabupaten" to kabupaten,
                "kecamatan" to kecamatan,
                "kelurahan" to kelurahan,
                "address" to address,
                "orderType" to "BeeWash",
                "option" to option,
                "date" to formattedDate,
                "qty" to qty.toInt(),
                "status" to bankName,
                "priceTotal" to priceTotal,
                "driverId" to "",
                "driverName" to "",
                "driverNumber" to "",
                "paymentProof" to dp,
                "driverImage" to "",
                "userNumber" to phone,
            )


            Timer().schedule(2000) {

                FirebaseFirestore
                    .getInstance()
                    .collection("order")
                    .document(orderId)
                    .set(order)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val job = GlobalScope.launch(Dispatchers.Default) {
                                SendNotification.sendNotificationFromUserToItself(myUid)
                                delay(1000)
                            }
                            runBlocking {
                                job.join()
                                mProgressDialog.dismiss()
                                showSuccessDialog()
                            }

                        } else {
                            mProgressDialog.dismiss()
                            showFailureDialog()
                        }
                    }
            }
        }
    }

    private fun getToken(username: String, option: String) {

        notificationUid = if(option == "user") {
            myUid
        } else {
            "CSpWB7SLOIQQ3eSjMVDpdC7Q8Yd2"
        }

        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(notificationUid!!)
            .get()
            .addOnSuccessListener {
                val token = "" + it.data?.get("token")

                if(option == "user") {
                    PushNotification(
                        NotificationData(
                            "Order Berhasil",
                            "Silahkan tunggu mitra BeeFlo akan menghapiri anda"
                        ),
                        token
                    ).also { pushNotification ->
                        sendNotification(pushNotification)
                    }

                    sendNotificationFromUserToMitra()
                } else {
                    Log.e("tag", token)
                    PushNotification(
                        NotificationData(
                            "Konfirmasi Pembayaran Kustomer",
                            "$username telah melakukan transfer pembayaran"
                        ),
                        token
                    ).also { pushNotification ->
                        sendNotification(pushNotification)
                    }
                }
            }
    }

    private fun sendNotificationFromUserToMitra() {
        val df = SimpleDateFormat("dd-MMM-yyyy, HH:mm:ss")
        val formattedDate: String = df.format(Date())


        FirebaseFirestore
            .getInstance()
            .collection("users")
            .whereEqualTo("role", "driver")
            .whereEqualTo("locKecamatan", kecamatan)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    val driverToken = "" + document.data["token"]
                    val driverUID = "" + document.data["uid"]
                    PushNotification(
                        NotificationData(
                            "Ada order baru",
                            "Order BeeWash menunggu anda"
                        ),
                        driverToken
                    ).also { pushNotification ->
                        sendNotification(pushNotification)
                    }
                    saveNotificationFromUserToMitra(formattedDate, driverUID)
                }
            }
    }

    private fun saveNotificationFromUserToMitra(formattedDate: String, driverUID: String) {
        val uid = System.currentTimeMillis().toString()

        val data = mapOf(
            "title" to "Ada order baru",
            "message" to "Order BeeWash menunggu anda",
            "date" to formattedDate,
            "type" to "driver",
            "userId" to driverUID,
            "uid" to uid
        )

        FirebaseFirestore
            .getInstance()
            .collection("notification")
            .document(uid)
            .set(data)
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.pushNotification(notification)
                runOnUiThread {
                    if (!response.isSuccessful) {
                        Log.e("Error else", response.body().toString())
                        Toast.makeText(this@BeeWashActivity, "Token kosong, mohon pastikan koneksi internet anda stabil dan coba lagi", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("Error catch", e.toString())
                runOnUiThread {
                    Toast.makeText(this@BeeWashActivity, "Token kosong, mohon pastikan koneksi internet anda stabil dan coba lagi", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun sendNotificationToAdmin(fullname: String, userId: String, bankName: String?) {

        val df = SimpleDateFormat("dd-MMM-yyyy, HH:mm:ss")
        val formattedDate: String = df.format(Date())
        val uid = System.currentTimeMillis().toString()

        val data = mapOf(
            "title" to "Konfirmasi Bukti Pembayaran",
            "message" to "$fullname telah mentransfer uang ke rekening $bankName, silahkan lakukan verifikasi",
            "date" to formattedDate,
            "type" to "admin",
            "userId" to userId,
            "uid" to uid
        )

        FirebaseFirestore
            .getInstance()
            .collection("notification")
            .document(uid)
            .set(data)
    }


    private fun getUserInformation() {
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(myUid)
            .get()
            .addOnSuccessListener {
                name = it.data?.get("username").toString()
                phone = it.data?.get("phone").toString()
                if(locationOption == "Sesuai Alamat Anda") {
                    provinsi = it.data?.get("locProvinsi").toString()
                    kabupaten = it.data?.get("locKabupaten").toString()
                    kecamatan = it.data?.get("locKecamatan").toString()
                    kelurahan = it.data?.get("locKelurahan").toString()
                }
            }
    }


    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Melakukan Order")
            .setMessage("Terdapat kesalahan ketika ingin melakukan order, silahkan periksa koneksi internet anda, dan coba lagi nanti")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Berhasil Melakukan Order")
            .setMessage("Sukses, silahkan cek lebih lanjut pada menu orderan\n\nTerima kasih.")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, i ->
                dialogInterface.dismiss()
                onBackPressed()
            }
            .show()
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





    private fun showProvinsi() {
        RetrofitClient.instance.getProvinsi().enqueue(object: Callback<ResponseProvinsi> {
            override fun onResponse(
                call: Call<ResponseProvinsi>,
                response: Response<ResponseProvinsi>
            ) {


                val listResponse = response.body()?.provinsi
                listResponse?.forEach {
                    listIdProv.add(it.id)
                    listNameProv.add(it.nama)
                }

                binding?.provinsi?.onItemSelectedListener = this@BeeWashActivity
                val adapter = ArrayAdapter(this@BeeWashActivity, android.R.layout.simple_spinner_dropdown_item, listNameProv)

                binding?.provinsi?.adapter = adapter
            }

            override fun onFailure(call: Call<ResponseProvinsi>, t: Throwable) {
                Toast.makeText(this@BeeWashActivity, "${t.message}", Toast.LENGTH_LONG).show()
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

                binding?.kota?.onItemSelectedListener = this@BeeWashActivity
                val adapter = ArrayAdapter(this@BeeWashActivity, android.R.layout.simple_spinner_dropdown_item, listNameKota)
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

                binding?.kecamatan?.onItemSelectedListener = this@BeeWashActivity
                val adapter = ArrayAdapter(
                    this@BeeWashActivity,
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
        RetrofitClient.instance.getKelurahan(idKecamatan).enqueue(object :
            Callback<ResponseKelurahan> {
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

                binding?.kelurahan?.onItemSelectedListener = this@BeeWashActivity
                val adapter = ArrayAdapter(
                    this@BeeWashActivity,
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
}