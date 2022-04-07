package com.project.beeapp.ui.homepage.ui.home.promotion

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PromotionModel(
    var uid: String? = null,
    var image: String? = null,
    var type: String? = null,
) : Parcelable