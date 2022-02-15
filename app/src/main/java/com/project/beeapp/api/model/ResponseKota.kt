package com.project.beeapp.api.model

import com.google.gson.annotations.SerializedName

data class ResponseKota(

	@field:SerializedName("kota_kabupaten")
	val kotaKabupaten: List<KotaKabupatenItem>
)

data class KotaKabupatenItem(

	@field:SerializedName("nama")
	val nama: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("id_provinsi")
	val idProvinsi: String
)
