package com.project.beeapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.project.beeapp.api.RetrofitClient
import com.project.beeapp.api.model.ResponseKecamatan
import com.project.beeapp.api.model.ResponseKelurahan
import com.project.beeapp.api.model.ResponseKota
import com.project.beeapp.api.model.ResponseProvinsi
import com.project.beeapp.databinding.ActivityRegisterBinding
import com.project.beeapp.notification.FirebaseService
import com.project.beeapp.notification.NotificationData
import com.project.beeapp.notification.PushNotification
import com.project.beeapp.notification.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RegisterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var binding: ActivityRegisterBinding? = null
    private val TAG = RegisterActivity::class.java.simpleName
    private var listIdProv = ArrayList<Int>()
    private var listNameProv = ArrayList<String>()
    private var listIdKota = ArrayList<Int>()
    private var listNameKota = ArrayList<String>()
    private var listIdKec = ArrayList<Int>()
    private var listNameKec = ArrayList<String>()
    private var listIdKel = ArrayList<Int>()
    private var listNameKel = ArrayList<String>()

    private var dp: String? = null
    private val REQUEST_FROM_GALLERY = 1001
    private var role: String? = null
    private lateinit var status: String
    private var myToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
            FirebaseService.token = result
            myToken = result
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        showProvinsi()


        binding?.registerBtn?.setOnClickListener {
            formValidation()
        }

        // KLIK TAMBAH GAMBAR
        binding?.imageHint?.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_FROM_GALLERY);
        }

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }


    }

    private fun formValidation() {
        val username = binding?.username?.text.toString().trim()
        val email = binding?.email?.text.toString().trim()
        val phone = binding?.phone?.text.toString().trim()
        val password = binding?.password?.text.toString().trim()
        val fullname = binding?.fullName?.text.toString().trim()
        val npwp = binding?.npwp?.text.toString().trim()

        when {
            username.isEmpty() -> {
                Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            email.isEmpty() -> {
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            !email.contains("@") || !email.contains(".") -> {
                Toast.makeText(this, "Format email salah", Toast.LENGTH_SHORT).show()
                return
            }
            phone.isEmpty() -> {
                Toast.makeText(this, "No.Telepon tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            password.isEmpty() -> {
                Toast.makeText(this, "Kata sandi tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            password.length < 6 -> {
                Toast.makeText(this, "Kata sandi minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return
            }
            role == null -> {
                Toast.makeText(
                    this,
                    "Anda ingin mendaftar sebagai kustomer atau sebagai mitra/driver ?, silahkan pilih",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            role == "driver" && dp == null -> {
                Toast.makeText(this, "Silahkan unggah foto formal anda", Toast.LENGTH_SHORT).show()
                return
            }
        }

        binding?.progressBar?.visibility = View.VISIBLE
        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    val userId = FirebaseAuth.getInstance().currentUser!!.uid

                    status = if (role == "driver") {
                        "Menunggu"
                    } else {
                        "user"
                    }

                    val data = mapOf(
                        "uid" to userId,
                        "username" to username,
                        "email" to email,
                        "password" to password,
                        "phone" to phone,
                        "role" to role,
                        "locProvinsi" to binding?.provinsi?.selectedItem.toString(),
                        "locKabupaten" to binding?.kota?.selectedItem.toString(),
                        "locKecamatan" to binding?.kecamatan?.selectedItem.toString(),
                        "locKelurahan" to binding?.kelurahan?.selectedItem.toString(),
                        "fullname" to "" + fullname,
                        "npwp" to "" + npwp,
                        "image" to "" + dp,
                        "status" to status,
                        "isWork" to false,
                        "token" to myToken.toString(),
                    )

                    FirebaseFirestore
                        .getInstance()
                        .collection("users")
                        .document(userId)
                        .set(data)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                if(role == "driver") {
                                    if(myToken != null) {
                                        sendNotificationToAdmin(fullname, userId)
                                        getAdminToken(fullname)
                                    } else {
                                        binding?.progressBar?.visibility = View.GONE
                                        showFailureDialog("Token kosong, mohon pastikan koneksi internet anda stabil dan coba lagi")
                                    }
                                } else {
                                    binding?.progressBar?.visibility = View.GONE
                                    showSuccessDialog()
                                }


                            } else {
                                binding?.progressBar?.visibility = View.GONE
                                showFailureDialog("Silahkan mendaftar kembali dengan informasi yang benar, dan pastikan koneksi internet lancar")
                            }
                        }
                } else {
                    binding?.progressBar?.visibility = View.GONE
                    try {
                        throw it.exception!!
                    } catch (e: FirebaseAuthUserCollisionException) {
                        showFailureDialog("Email yang anda daftarkan sudah digunakan, silahkan coba email lain")
                    } catch (e: java.lang.Exception) {
                        Log.e("TAG", e.message!!)
                    }
                }
            }
    }

    private fun sendNotificationToAdmin(fullname: String, userId: String) {

        val df = SimpleDateFormat("dd-MMM-yyyy, HH:mm:ss")
        val formattedDate: String = df.format(Date())
        val uid = System.currentTimeMillis().toString()

        val data = mapOf(
            "title" to "Konfirmasi Pendaftaran Mitra",
            "message" to "$fullname telah mendaftar sebagai mitra",
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

    private fun getAdminToken(fullname: String) {
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document("CSpWB7SLOIQQ3eSjMVDpdC7Q8Yd2")
            .get()
            .addOnSuccessListener {
                val token = "" + it.data?.get("token")

                PushNotification(
                    NotificationData(
                        "Konfirmasi Pendaftaran Mitra",
                        "$fullname telah mendaftar sebagai mitra"
                    ),
                    token
                ).also { pushNotification ->
                    sendNotification(pushNotification)
                }
            }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.id) {
                R.id.user ->
                    if (checked) {
                        role = "user"
                        binding?.privateInformation?.visibility = View.GONE
                    }
                R.id.driver ->
                    if (checked) {
                        role = "driver"
                        binding?.privateInformation?.visibility = View.VISIBLE
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

                binding?.provinsi?.onItemSelectedListener = this@RegisterActivity
                val adapter = ArrayAdapter(
                    this@RegisterActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    listNameProv
                )

                binding?.provinsi?.adapter = adapter
            }

            override fun onFailure(call: Call<ResponseProvinsi>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "${t.message}", Toast.LENGTH_LONG).show()
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

                binding?.kota?.onItemSelectedListener = this@RegisterActivity
                val adapter = ArrayAdapter(
                    this@RegisterActivity,
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

                binding?.kecamatan?.onItemSelectedListener = this@RegisterActivity
                val adapter = ArrayAdapter(
                    this@RegisterActivity,
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

                    binding?.kelurahan?.onItemSelectedListener = this@RegisterActivity
                    val adapter = ArrayAdapter(
                        this@RegisterActivity,
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


    /// munculkan dialog ketika gagal registrasi
    private fun showFailureDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Gagal melakukan registrasi")
            .setMessage(message)
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    /// munculkan dialog ketika sukses registrasi
    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Berhasil melakukan registrasi")
            .setMessage("Admin akan memverifikasi pendaftaran anda, silahkan menunggu beberapa hari kedepan")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
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
        val imageFileName = "driver/image_" + System.currentTimeMillis() + ".png"
        mStorageRef.child(imageFileName).putFile(data!!)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        mProgressDialog.dismiss()
                        dp = uri.toString()
                        Glide
                            .with(this)
                            .load(dp)
                            .into(binding!!.image)
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


    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.pushNotification(notification)
                runOnUiThread {
                    if (response.isSuccessful) {
                        binding?.progressBar?.visibility = View.GONE
                        showSuccessDialog()
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                        Log.e("Error else", response.body().toString())
                        showFailureDialog("Token kosong, mohon pastikan koneksi internet anda stabil dan coba lagi")
                    }
                }
            } catch (e: Exception) {
                binding?.progressBar?.visibility = View.GONE
                Log.e("Error catch", e.toString())
                runOnUiThread {
                    showFailureDialog("Token kosong, mohon pastikan koneksi internet anda stabil dan coba lagi")
                }
            }
        }

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


    companion object {
        const val TOPIC = "/topics/register"
    }

}