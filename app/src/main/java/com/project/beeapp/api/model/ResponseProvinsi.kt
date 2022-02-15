package com.project.beeapp.api.model

import com.google.gson.annotations.SerializedName

data class ResponseProvinsi(

	@field:SerializedName("provinsi")
	val provinsi: List<ProvinsiItem>
)

data class ProvinsiItem(

	@field:SerializedName("nama")
	val nama: String,

	@field:SerializedName("id")
	val id: Int
)
