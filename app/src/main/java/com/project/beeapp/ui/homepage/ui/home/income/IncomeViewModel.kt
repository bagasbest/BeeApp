package com.project.beeapp.ui.homepage.ui.home.income

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class IncomeViewModel : ViewModel() {

    private val incomeList = MutableLiveData<ArrayList<IncomeModel>>()
    private val listItems = ArrayList<IncomeModel>()
    private val TAG = IncomeViewModel::class.java.simpleName

    fun setListIncome(partnerId: String) {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("income")
                .whereEqualTo("partnerId", partnerId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = IncomeModel()
                        model.income = document.data["income"] as Long
                        model.orderId = document.data["orderId"].toString()
                        model.orderType = document.data["orderType"].toString()
                        model.partnerId = document.data["partnerId"].toString()
                        model.date = document.data["date"].toString()
                        model.dateTimeInMillis = document.data["dateTimeInMillis"] as Long

                        listItems.add(model)
                    }
                    incomeList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getIncome() : LiveData<ArrayList<IncomeModel>> {
        return incomeList
    }


}