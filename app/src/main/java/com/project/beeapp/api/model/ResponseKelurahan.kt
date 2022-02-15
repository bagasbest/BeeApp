package com.project.beeapp.api.model

import com.google.gson.annotations.SerializedName

data class ResponseKelurahan(

	@field:SerializedName("kelurahan")
	val kelurahan: List<KelurahanItem>
)

data class KelurahanItem(

	@field:SerializedName("id_kecamatan")
	val idKecamatan: String,

	@field:SerializedName("nama")
	val nama: String,

	@field:SerializedName("id")
	val id: Int,
)
