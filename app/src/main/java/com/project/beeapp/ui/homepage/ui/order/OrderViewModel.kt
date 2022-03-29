package com.project.beeapp.ui.homepage.ui.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class OrderViewModel : ViewModel() {

    private val orderList = MutableLiveData<ArrayList<OrderModel>>()
    private val listItems = ArrayList<OrderModel>()
    private val TAG = OrderViewModel::class.java.simpleName

    fun setListOrderProcess(myUid: String) {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("userId", myUid)
                .whereEqualTo("status", "Order Diterima")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = OrderModel()
                        model.orderId = document.data["orderId"].toString()
                        model.userId = document.data["userId"].toString()
                        model.username = document.data["username"].toString()
                        model.status = document.data["status"].toString()
                        model.option = document.data["option"].toString()
                        model.provinsi = document.data["provinsi"].toString()
                        model.kabupaten = document.data["kabupaten"].toString()
                        model.kecamatan = document.data["kecamatan"].toString()
                        model.kelurahan = document.data["kelurahan"].toString()
                        model.address = document.data["address"].toString()
                        model.date = document.data["date"].toString()
                        model.qty = document.data["qty"].toString().toInt()
                        model.priceTotal = document.data["priceTotal"] as Long
                        model.orderType = document.data["orderType"].toString()
                        model.driverId = document.data["driverId"].toString()
                        model.driverName = document.data["driverName"].toString()
                        model.driverNumber = document.data["driverNumber"].toString()
                        model.driverImage = document.data["driverImage"].toString()
                        model.paymentProof = document.data["paymentProof"].toString()
                        model.userNumber = document.data["userNumber"].toString()

                        listItems.add(model)
                    }
                    orderList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListOrderFinish(myUID: String) {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("userId", myUID)
                .whereEqualTo("status", "Selesai")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = OrderModel()
                        model.orderId = document.data["orderId"].toString()
                        model.userId = document.data["userId"].toString()
                        model.username = document.data["username"].toString()
                        model.status = document.data["status"].toString()
                        model.option = document.data["option"].toString()
                        model.provinsi = document.data["provinsi"].toString()
                        model.kabupaten = document.data["kabupaten"].toString()
                        model.kecamatan = document.data["kecamatan"].toString()
                        model.kelurahan = document.data["kelurahan"].toString()
                        model.address = document.data["address"].toString()
                        model.date = document.data["date"].toString()
                        model.qty = document.data["qty"].toString().toInt()
                        model.priceTotal = document.data["priceTotal"] as Long
                        model.orderType = document.data["orderType"].toString()
                        model.driverId = document.data["driverId"].toString()
                        model.driverName = document.data["driverName"].toString()
                        model.driverNumber = document.data["driverNumber"].toString()
                        model.driverImage = document.data["driverImage"].toString()
                        model.paymentProof = document.data["paymentProof"].toString()
                        model.userNumber = document.data["userNumber"].toString()

                        listItems.add(model)
                    }
                    orderList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun setListOrderProcessByDriver(driverLocKecamatan: String?) {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("kecamatan", driverLocKecamatan)
                .whereIn("status", listOf("Cash","Sudah Bayar"))
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = OrderModel()
                        model.orderId = document.data["orderId"].toString()
                        model.userId = document.data["userId"].toString()
                        model.username = document.data["username"].toString()
                        model.status = document.data["status"].toString()
                        model.option = document.data["option"].toString()
                        model.provinsi = document.data["provinsi"].toString()
                        model.kabupaten = document.data["kabupaten"].toString()
                        model.kecamatan = document.data["kecamatan"].toString()
                        model.kelurahan = document.data["kelurahan"].toString()
                        model.address = document.data["address"].toString()
                        model.date = document.data["date"].toString()
                        model.qty = document.data["qty"].toString().toInt()
                        model.priceTotal = document.data["priceTotal"] as Long
                        model.orderType = document.data["orderType"].toString()
                        model.driverId = document.data["driverId"].toString()
                        model.driverName = document.data["driverName"].toString()
                        model.driverNumber = document.data["driverNumber"].toString()
                        model.driverImage = document.data["driverImage"].toString()
                        model.paymentProof = document.data["paymentProof"].toString()
                        model.userNumber = document.data["userNumber"].toString()

                        listItems.add(model)
                    }
                    orderList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListOrderProcessByPick(myUID: String) {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("userId", myUID)
                .whereIn("status", listOf("Cash", "Bank BNI", "Bank BCA", "Sudah Bayar"))
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = OrderModel()
                        model.orderId = document.data["orderId"].toString()
                        model.userId = document.data["userId"].toString()
                        model.username = document.data["username"].toString()
                        model.status = document.data["status"].toString()
                        model.option = document.data["option"].toString()
                        model.provinsi = document.data["provinsi"].toString()
                        model.kabupaten = document.data["kabupaten"].toString()
                        model.kecamatan = document.data["kecamatan"].toString()
                        model.kelurahan = document.data["kelurahan"].toString()
                        model.address = document.data["address"].toString()
                        model.date = document.data["date"].toString()
                        model.qty = document.data["qty"].toString().toInt()
                        model.priceTotal = document.data["priceTotal"] as Long
                        model.orderType = document.data["orderType"].toString()
                        model.driverId = document.data["driverId"].toString()
                        model.driverName = document.data["driverName"].toString()
                        model.driverNumber = document.data["driverNumber"].toString()
                        model.driverImage = document.data["driverImage"].toString()
                        model.paymentProof = document.data["paymentProof"].toString()
                        model.userNumber = document.data["userNumber"].toString()

                        listItems.add(model)
                    }
                    orderList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListOrderProcessByAdmin() {
        listItems.clear()

        try {
            FirebaseFirestore.getInstance().collection("order")
                .whereIn("status", listOf("Bank BCA", "Bank BNI"))
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = OrderModel()
                        model.orderId = document.data["orderId"].toString()
                        model.userId = document.data["userId"].toString()
                        model.username = document.data["username"].toString()
                        model.status = document.data["status"].toString()
                        model.option = document.data["option"].toString()
                        model.provinsi = document.data["provinsi"].toString()
                        model.kabupaten = document.data["kabupaten"].toString()
                        model.kecamatan = document.data["kecamatan"].toString()
                        model.kelurahan = document.data["kelurahan"].toString()
                        model.address = document.data["address"].toString()
                        model.date = document.data["date"].toString()
                        model.qty = document.data["qty"].toString().toInt()
                        model.priceTotal = document.data["priceTotal"] as Long
                        model.orderType = document.data["orderType"].toString()
                        model.driverId = document.data["driverId"].toString()
                        model.driverName = document.data["driverName"].toString()
                        model.driverNumber = document.data["driverNumber"].toString()
                        model.driverImage = document.data["driverImage"].toString()
                        model.paymentProof = document.data["paymentProof"].toString()
                        model.userNumber = document.data["userNumber"].toString()

                        listItems.add(model)
                    }
                    orderList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }



    fun setListOrderProcessByDriverOnGoing(myUID: String) {

        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("driverId", myUID)
                .whereNotIn("status", listOf("Menunggu", "Selesai"))
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = OrderModel()
                        model.orderId = document.data["orderId"].toString()
                        model.userId = document.data["userId"].toString()
                        model.username = document.data["username"].toString()
                        model.status = document.data["status"].toString()
                        model.option = document.data["option"].toString()
                        model.provinsi = document.data["provinsi"].toString()
                        model.kabupaten = document.data["kabupaten"].toString()
                        model.kecamatan = document.data["kecamatan"].toString()
                        model.kelurahan = document.data["kelurahan"].toString()
                        model.address = document.data["address"].toString()
                        model.date = document.data["date"].toString()
                        model.qty = document.data["qty"].toString().toInt()
                        model.priceTotal = document.data["priceTotal"] as Long
                        model.orderType = document.data["orderType"].toString()
                        model.driverId = document.data["driverId"].toString()
                        model.driverName = document.data["driverName"].toString()
                        model.driverNumber = document.data["driverNumber"].toString()
                        model.driverImage = document.data["driverImage"].toString()
                        model.paymentProof = document.data["paymentProof"].toString()
                        model.userNumber = document.data["userNumber"].toString()


                        listItems.add(model)
                    }
                    orderList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListOrderProcessByDriverFinish(myUID: String) {

        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("driverId", myUID)
                .whereEqualTo("status", "Selesai")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = OrderModel()
                        model.orderId = document.data["orderId"].toString()
                        model.userId = document.data["userId"].toString()
                        model.username = document.data["username"].toString()
                        model.status = document.data["status"].toString()
                        model.option = document.data["option"].toString()
                        model.provinsi = document.data["provinsi"].toString()
                        model.kabupaten = document.data["kabupaten"].toString()
                        model.kecamatan = document.data["kecamatan"].toString()
                        model.kelurahan = document.data["kelurahan"].toString()
                        model.address = document.data["address"].toString()
                        model.date = document.data["date"].toString()
                        model.qty = document.data["qty"].toString().toInt()
                        model.priceTotal = document.data["priceTotal"] as Long
                        model.orderType = document.data["orderType"].toString()
                        model.driverId = document.data["driverId"].toString()
                        model.driverName = document.data["driverName"].toString()
                        model.driverNumber = document.data["driverNumber"].toString()
                        model.driverImage = document.data["driverImage"].toString()
                        model.paymentProof = document.data["paymentProof"].toString()
                        model.userNumber = document.data["userNumber"].toString()

                        listItems.add(model)
                    }
                    orderList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun getOrderList() : LiveData<ArrayList<OrderModel>> {
        return orderList
    }



}