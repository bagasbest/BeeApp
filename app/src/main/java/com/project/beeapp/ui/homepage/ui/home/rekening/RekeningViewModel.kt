package com.project.beeapp.ui.homepage.ui.home.rekening

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class RekeningViewModel  :ViewModel() {


    private val rekeningList = MutableLiveData<ArrayList<RekeningModel>>()
    private val listItems = ArrayList<RekeningModel>()
    private val TAG = RekeningViewModel::class.java.simpleName

    fun setListRekening() {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("payment")
                .whereNotEqualTo("bankName", "Cash")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = RekeningModel()
                        model.bankName = document.data["bankName"].toString()
                        model.recName = document.data["recName"].toString()
                        model.recNumber = document.data["recNumber"].toString()
                        model.uid = document.data["uid"].toString()
                        listItems.add(model)
                    }
                    rekeningList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getRekeningList() : LiveData<ArrayList<RekeningModel>> {
        return rekeningList
    }

}