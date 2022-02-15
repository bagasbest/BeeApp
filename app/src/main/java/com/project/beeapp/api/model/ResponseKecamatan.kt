package com.project.beeapp.api.model

import com.google.gson.annotations.SerializedName

data class ResponseKecamatan(

	@field:SerializedName("kecamatan")
	val kecamatan: List<KecamatanItem>
)

data class KecamatanItem(

	@field:SerializedName("nama")
	val nama: String,

	@field:SerializedName("id_kota")
	val idKota: String,

	@field:SerializedName("id")
	val id: Int
)
