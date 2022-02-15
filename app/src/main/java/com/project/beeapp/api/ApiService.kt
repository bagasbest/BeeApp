package com.project.beeapp.api

import com.project.beeapp.api.model.ResponseKecamatan
import com.project.beeapp.api.model.ResponseKelurahan
import com.project.beeapp.api.model.ResponseKota
import com.project.beeapp.api.model.ResponseProvinsi
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("provinsi")
    fun getProvinsi() : Call<ResponseProvinsi>


    @GET("kota")
    fun getKota(@Query("id_provinsi") id_provinsi: Int) : Call<ResponseKota>

    @GET("kecamatan")
    fun getKecamatan(@Query("id_kota") id_kota: Int) : Call<ResponseKecamatan>


    @GET("kelurahan")
    fun getKelurahan(@Query("id_kecamatan") id_kecamatan : Int) : Call<ResponseKelurahan>

}