package com.project.beeapp.ui.homepage.ui.home.admin_daerah

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AdminModel(
    var uid: String? = null,
    var fullname: String? = null,
    var username: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var npwp: String? = null,
    var image: String? = null,
    var status: String? = null,
    var locationTask: ArrayList<String>? = null,
) : Parcelable