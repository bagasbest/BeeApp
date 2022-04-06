package com.project.beeapp.ui.homepage.ui.home.admin_daerah

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.project.beeapp.ui.homepage.ui.home.verify_driver.VerifyDriverViewModel

class AdminViewModel : ViewModel() {

    private val adminList = MutableLiveData<ArrayList<AdminModel>>()
    private val listItems = ArrayList<AdminModel>()
    private val TAG = VerifyDriverViewModel::class.java.simpleName

    fun setListAdmin() {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("role", "adminKecamatan")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = AdminModel()
                        model.email = document.data["email"].toString()
                        model.fullname = document.data["fullname"].toString()
                        model.username = document.data["username"].toString()
                        model.image = document.data["image"].toString()
                        model.npwp = document.data["npwp"].toString()
                        model.phone = document.data["phone"].toString()
                        model.status = document.data["status"].toString()
                        model.uid = document.data["uid"].toString()
                        model.locationTask = document.data["locationTask"] as ArrayList<String>

                        listItems.add(model)
                    }
                    adminList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getAdminList() : LiveData<ArrayList<AdminModel>> {
        return adminList
    }
}