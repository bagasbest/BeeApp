package com.project.beeapp.ui.homepage.ui.home.income


data class IncomeModel(
    var orderId: String? = null,
    var partnerId: String? = null,
    var orderType: String? = null,
    var date: String? = null,
    var dateTimeInMillis: Long? = 0L,
    var income: Long? = 0L,
)