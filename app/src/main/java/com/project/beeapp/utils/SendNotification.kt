package com.project.beeapp.utils

import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

object SendNotification {

    fun sendNotificationFromUserToItself(myUid: String) {
        val df = SimpleDateFormat("dd-MMM-yyyy, HH:mm:ss")
        val formattedDate: String = df.format(Date())
        val uid = System.currentTimeMillis().toString()

        val data = mapOf(
            "title" to "Konfirmasi Bukti Pembayaran",
            "message" to "Anda berhasil melakukan order, jika anda melakukan pembayaran via Bank, maka silahkan menunggu admin melakukan verifikasi\n\nJika pembayaran melalui Cash, silahkan menunggu mitra BeeFlo menerima orderan anda",
            "date" to formattedDate,
            "type" to "user",
            "userId" to myUid,
            "uid" to uid
        )

        FirebaseFirestore
            .getInstance()
            .collection("notification")
            .document(uid)
            .set(data)
    }

}