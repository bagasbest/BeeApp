package com.project.beeapp.ui.homepage.ui.home.verify_driver

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class VerifyDriverViewModel : ViewModel() {

    private val driverList = MutableLiveData<ArrayList<VerifyDriverModel>>()
    private val listItems = ArrayList<VerifyDriverModel>()
    private val TAG = VerifyDriverViewModel::class.java.simpleName

    fun setListDriver() {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("role", "driver")
                .orderBy("status", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = VerifyDriverModel()
                        model.email = document.data["email"].toString()
                        model.fullname = document.data["fullname"].toString()
                        model.username = document.data["username"].toString()
                        model.image = document.data["image"].toString()
                        model.locKabupaten = document.data["locKabupaten"].toString()
                        model.locProvinsi = document.data["locProvinsi"].toString()
                        model.locKecamatan = document.data["locKecamatan"].toString()
                        model.locKelurahan = document.data["locKelurahan"].toString()
                        model.npwp = document.data["npwp"].toString()
                        model.phone = document.data["phone"].toString()
                        model.status = document.data["status"].toString()
                        model.uid = document.data["uid"].toString()


                        listItems.add(model)
                    }
                    driverList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getDriverList() : LiveData<ArrayList<VerifyDriverModel>> {
        return driverList
    }

}