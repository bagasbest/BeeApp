package com.project.beeapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.project.beeapp.databinding.ActivityMainBinding
import com.project.beeapp.notification.FirebaseService
import com.project.beeapp.ui.homepage.HomeActivity

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        autoLogin()

        binding?.registerBtn?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding?.loginBtn?.setOnClickListener {
            formValidation()
        }

    }

    private fun formValidation() {
        val username = binding?.username?.text.toString().trim()
        val password = binding?.password?.text.toString().trim()

        if(username.isEmpty()) {
            Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        } else if(password.isEmpty()) {
            Toast.makeText(this, "Kata sandi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }


        binding?.progressBar?.visibility = View.VISIBLE

        FirebaseFirestore
            .getInstance()
            .collection("users")
            .whereEqualTo("username", username)
            .limit(1)
            .get()
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.result.size() == 0) {
                    /// jika tidak terdapat di database dan email serta password, maka tidak bisa login
                    binding?.progressBar?.visibility = View.GONE
                    showFailureDialog()
                    return@OnCompleteListener
                }

                /// jika terdaftar maka ambil email di database, kemudian lakukan autentikasi menggunakan email & password dari user
                for (snapshot in task.result) {
                    val email = "" + snapshot["email"]
                    val status = "" + snapshot["status"]
                    val role = "" + snapshot["role"]
                    val auth = FirebaseAuth.getInstance()

                    if(role != "driver"){
                        authUser(email, password)
                    } else {
                        when (status) {
                            "Aktif" -> {
                                authUser(email, password)
                            }
                            "Menunggu" -> {
                                binding?.progressBar?.visibility = View.GONE
                                auth.signOut()
                                showDriverStatusDialog("Akun Belum Diverifikasi", "Terima kasih telah bergabung menjadi mitra BeeFlo, Admin BeeFlo sedang memverifikasi pendaftaran akun anda, silahkan menunggu\n\nTerima Kasih")
                            }
                            "Blokir" -> {
                                binding?.progressBar?.visibility = View.GONE
                                auth.signOut()
                                showDriverStatusDialog("Akun Dibekukan", "Mohon maaf, akun anda di bekukan beberapa saat karena terindikasi melakukan tindak kecurangan\n\nSilahkan hubungi contact person BeeFlo untuk menindaklanjuti permasalahan ini\n\nTerima kasih")
                            }
                            "PHK" -> {
                                binding?.progressBar?.visibility = View.GONE
                                auth.signOut()
                                showDriverStatusDialog("Akun Dibekukan Selamanya", "Maaf, akun anda dibekukan selamanya karena terbukri melakukan beberapa kecurangan\n\nTerima Kasih.")
                            }
                            "Ditolak" -> {
                                binding?.progressBar?.visibility = View.GONE
                                auth.signOut()
                                showDriverStatusDialog("Pendaftaran Anda Ditolak", "Maaf, informasi yang anda masukkan ketika pendaftaran tidak sesuai dengan kriteria driver BeeFlo\n\nDengan berat hati kami meolak pendaftaran anda.")
                            }
                        }
                    }

                }
            })

    }

    private fun authUser(email: String, password: String) {
        /// fungsi untuk mengecek, apakah email yang di inputkan ketika login sudah terdaftar di database atau belum
        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task2 ->
                if (task2.isSuccessful) {

                    FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
                    FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
                        FirebaseService.token = result

                        val myUid = FirebaseAuth.getInstance().currentUser!!.uid
                        FirebaseFirestore
                            .getInstance()
                            .collection("users")
                            .document(myUid)
                            .update("token", result.toString())
                            .addOnCompleteListener {
                                /// jika terdapat di database dan email serta password sama, maka masuk ke homepage
                                binding?.progressBar?.visibility = View.GONE
                                startActivity(Intent(this, HomeActivity::class.java))
                            }
                    }
                } else {
                    /// jika tidak terdapat di database dan email serta password, maka tidak bisa login
                    binding?.progressBar?.visibility = View.GONE
                    showFailureDialog()
                }
            }
    }


    private fun autoLogin() {
        if(FirebaseAuth.getInstance().currentUser != null) {
            Log.e("tag", "sasaas")
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    /// munculkan dialog ketika gagal login
    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal melakukan login")
            .setMessage("Silahkan login kembali dengan informasi yang benar")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ -> dialogInterface.dismiss() }
            .show()
    }

    private fun showDriverStatusDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("OKE") { dialogInterface, _ -> dialogInterface.dismiss() }
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}