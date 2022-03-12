package com.project.beeapp.ui.homepage.ui.home.verify_driver

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VerifyDriverModel(
    var uid: String? = null,
    var username: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var locProvinsi: String? = null,
    var locKabupaten: String? = null,
    var locKecamatan: String? = null,
    var locKelurahan: String? = null,
    var fullname: String? = null,
    var npwp: String? = null,
    var image: String? = null,
    var status: String? = null
) : Parcelable