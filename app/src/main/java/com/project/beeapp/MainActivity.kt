package com.project.beeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.databinding.ActivityMainBinding
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

                    /// fungsi untuk mengecek, apakah email yang di inputkan ketika login sudah terdaftar di database atau belum
                    FirebaseAuth
                        .getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                /// jika terdapat di database dan email serta password sama, maka masuk ke homepage
                                binding?.progressBar?.visibility = View.GONE
                                startActivity(Intent(this, HomeActivity::class.java))
                            } else {
                                /// jika tidak terdapat di database dan email serta password, maka tidak bisa login
                                binding?.progressBar?.visibility = View.GONE
                                showFailureDialog()
                            }
                        }
                }
            })

    }



    private fun autoLogin() {
        if(FirebaseAuth.getInstance().currentUser != null) {
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


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}