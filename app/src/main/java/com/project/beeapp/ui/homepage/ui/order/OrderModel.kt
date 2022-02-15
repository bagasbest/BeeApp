package com.project.beeapp.ui.homepage.ui.order

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderModel(

    var orderId: String? = null,
    var userId: String? = null,
    var username: String? = null,
    var provinsi: String? = null,
    var kabupaten: String? = null,
    var kecamatan: String? = null,
    var kelurahan: String? = null,
    var address: String? = null,
    var orderType: String? = null,
    var option: String? = null,
    var date: String? = null,
    var status: String? = null,
    var qty: Int? = 0,
    var priceTotal: Long? = 0,
    var driverId: String? = null,
    var driverName: String? = null,
    var driverNumber: String? = null,
    var driverImage: String? = null,
    var paymentProof: String? = null,

) : Parcelable