package com.project.beeapp.ui.homepage.ui.notification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class NotificationViewModel : ViewModel() {

    private val notificationList = MutableLiveData<ArrayList<NotificationModel>>()
    private val listItems = ArrayList<NotificationModel>()
    private val TAG = NotificationViewModel::class.java.simpleName

    fun setLisNotificationByAdmin() {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("notification")
                .whereEqualTo("type", "admin")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = NotificationModel()
                        model.date = document.data["date"].toString()
                        model.message = document.data["message"].toString()
                        model.title = document.data["title"].toString()
                        model.type = document.data["type"].toString()
                        model.uid = document.data["uid"].toString()
                        model.userId = document.data["userId"].toString()
                        listItems.add(model)
                    }
                    notificationList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setLisNotificationByUser(uid : String) {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("notification")
                .whereEqualTo("userId", uid)
                .whereEqualTo("type", "user")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = NotificationModel()
                        model.date = document.data["date"].toString()
                        model.message = document.data["message"].toString()
                        model.title = document.data["title"].toString()
                        model.type = document.data["type"].toString()
                        model.uid = document.data["uid"].toString()
                        model.userId = document.data["userId"].toString()
                        listItems.add(model)
                    }
                    notificationList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }



    fun setLisNotificationByMitra(uid : String) {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("notification")
                .whereEqualTo("userId", uid)
                .whereEqualTo("type", "driver")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = NotificationModel()
                        model.date = document.data["date"].toString()
                        model.message = document.data["message"].toString()
                        model.title = document.data["title"].toString()
                        model.type = document.data["type"].toString()
                        model.uid = document.data["uid"].toString()
                        model.userId = document.data["userId"].toString()
                        listItems.add(model)
                    }
                    notificationList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun getNotificationList() : LiveData<ArrayList<NotificationModel>> {
        return notificationList
    }

}