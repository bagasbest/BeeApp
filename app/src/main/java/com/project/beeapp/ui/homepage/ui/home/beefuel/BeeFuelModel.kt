package com.project.beeapp.ui.homepage.ui.home.beefuel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BeeFuelModel(

    var dex : Long? = 0L,
    var dexLite : Long? = 0L,
    var pertalite : Long? = 0L,
    var pertamax : Long? = 0L,
    var pertamaxTurbo : Long? = 0L,
    var premium : Long? = 0L,
    var solar : Long? = 0L,

) : Parcelable