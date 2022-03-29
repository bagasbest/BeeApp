package com.project.beeapp.ui.homepage.ui.home.beefuel

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
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
import androidx.appcompat.app.AppCompatActivity
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
import com.project.beeapp.databinding.ActivityBeeFuelBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class BeeFuelActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var binding: ActivityBeeFuelBinding? = null
    private val myUid = FirebaseAuth.getInstance().currentUser!!.uid
    private var provinsi: String? = null
    private var kabupaten: String? = null
    private var kecamatan: String? = null
    private var kelurahan: String? = null
    private var name: String? = null
    private var phone: String? = null
    private var priceTotal: Long? = 0L
    private var fuelType: String? = null
    private val format: NumberFormat = DecimalFormat("#,###")
    private var model = BeeFuelModel()

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


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeeFuelBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        checkRole()
        getPricing()
        setPaymentChoose()
        setLocationChoose()

        Glide.with(this)
            .load(R.drawable.logo_trans2)
            .into(binding!!.imageView3)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.fuel_type, android.R.layout.simple_list_item_1
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding?.fuelType?.setAdapter(adapter)
        binding?.fuelType?.setOnItemClickListener { _, _, _, _ ->
            fuelType = binding?.fuelType?.text.toString()
            binding?.liter?.setText("")
        }

        binding?.liter?.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(liter: Editable?) {
                if (liter.toString().isEmpty() || liter.toString().toInt() <= 0) {
                    priceTotal = 0
                    binding?.priceTotal?.text = "Total Biaya Rp.${priceTotal}"
                } else {

                    when (fuelType) {
                        "Dex" -> {
                            priceTotal = model.dex?.times(liter.toString().toLong())
                            binding?.priceTotal?.text = "Total Biaya Rp.${format.format(priceTotal)}"
                        }
                        "Dex Lite" -> {
                            priceTotal = model.dexLite?.times(liter.toString().toLong())
                            binding?.priceTotal?.text = "Total Biaya Rp.${format.format(priceTotal)}"
                        }
                        "Pertalite" -> {
                            priceTotal = model.pertalite?.times(liter.toString().toLong())
                            binding?.priceTotal?.text = "Total Biaya Rp.${format.format(priceTotal)}"
                        }
                        "Pertamax" -> {
                            priceTotal = model.pertamax?.times(liter.toString().toLong())
                            binding?.priceTotal?.text = "Total Biaya Rp.${format.format(priceTotal)}"
                        }
                        "Pertamax Turbo" -> {
                            priceTotal = model.pertamaxTurbo?.times(liter.toString().toLong())
                            binding?.priceTotal?.text = "Total Biaya Rp.${format.format(priceTotal)}"
                        }
                        "Premium" -> {
                            priceTotal = model.premium?.times(liter.toString().toLong())
                            binding?.priceTotal?.text = "Total Biaya Rp.${format.format(priceTotal)}"
                        }
                        "Solar" -> {
                            priceTotal = model.solar?.times(liter.toString().toLong())
                            binding?.priceTotal?.text = "Total Biaya Rp.${format.format(priceTotal)}"
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

        binding?.orderBtn?.setOnClickListener {
            formValidation()
        }

        binding?.edit?.setOnClickListener {
            val intent = Intent(this, BeeFuelEditActivity::class.java)
            intent.putExtra(BeeFuelEditActivity.EXTRA_PRICE_FUEL, model)
            startActivity(intent)
        }


        // KLIK TAMBAH GAMBAR
        binding?.imageHint?.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_FROM_GALLERY);
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
    }

    private fun getPricing() {
        FirebaseFirestore
            .getInstance()
            .collection("pricing")
            .document("beeFuel")
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    model.dex = it.data?.get("Dex") as Long
                    model.dexLite = it.data?.get("Dex Lite") as Long
                    model.pertalite = it.data?.get("Pertalite") as Long
                    model.pertamax = it.data?.get("Pertamax") as Long
                    model.pertamaxTurbo = it.data?.get("Pertamax Turbo") as Long
                    model.premium = it.data?.get("Premium") as Long
                    model.solar = it.data?.get("Solar") as Long
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

    @SuppressLint("SimpleDateFormat")
    private fun formValidation() {
        val address = binding?.address?.text.toString().trim()
        val liter = binding?.liter?.text.toString().trim()
        provinsi = binding?.provinsi?.selectedItem.toString()
        kabupaten = binding?.kota?.selectedItem.toString()
        kecamatan = binding?.kecamatan?.selectedItem.toString()
        kelurahan = binding?.kelurahan?.selectedItem.toString()

        when {
            address.isEmpty() -> {
                Toast.makeText(this, "Alamat lengkap tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            liter.isEmpty() -> {
                Toast.makeText(this, "Tentukan berapa liter yang anda inginkan", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            liter.toInt() < 3 -> {
                Toast.makeText(this, "Minimal order 3 Liter", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            fuelType == null -> {
                Toast.makeText(this, "Anda harus memilih jenis bahan bakar", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            bankName != "Cash" && dp == null -> {
                Toast.makeText(this, "Anda harus mengunggah bukti pembayaran, sebelum melakukan order", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            locationOption == "Lokasi Lain" -> {
                if(provinsi == null || kabupaten == null || kecamatan == null || kelurahan == null) {
                    Toast.makeText(this, "Silahkan pilih lokasi anda saat ini", Toast.LENGTH_SHORT)
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
            } else {
                sendNotificationToAdmin(name!!, myUid, bankName)
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
                "orderType" to "BeeFuel",
                "option" to fuelType,
                "date" to formattedDate,
                "qty" to liter.toInt(),
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
                            mProgressDialog.dismiss()
                            showSuccessDialog()
                        } else {
                            mProgressDialog.dismiss()
                            showFailureDialog()
                        }
                    }
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

                binding?.provinsi?.onItemSelectedListener = this@BeeFuelActivity
                val adapter = ArrayAdapter(this@BeeFuelActivity, android.R.layout.simple_spinner_dropdown_item, listNameProv)

                binding?.provinsi?.adapter = adapter
            }

            override fun onFailure(call: Call<ResponseProvinsi>, t: Throwable) {
                Toast.makeText(this@BeeFuelActivity, "${t.message}", Toast.LENGTH_LONG).show()
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

                binding?.kota?.onItemSelectedListener = this@BeeFuelActivity
                val adapter = ArrayAdapter(this@BeeFuelActivity, android.R.layout.simple_spinner_dropdown_item, listNameKota)
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

                binding?.kecamatan?.onItemSelectedListener = this@BeeFuelActivity
                val adapter = ArrayAdapter(
                    this@BeeFuelActivity,
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

                binding?.kelurahan?.onItemSelectedListener = this@BeeFuelActivity
                val adapter = ArrayAdapter(
                    this@BeeFuelActivity,
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