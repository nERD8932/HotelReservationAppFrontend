package com.smu.mcda.hotelreservationapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private const val BASE_URL = "https://nerd8932.pythonanywhere.com/"
    // private const val BASE_URL = "http://127.0.0.1:8000/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}