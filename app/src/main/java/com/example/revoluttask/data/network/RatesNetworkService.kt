package com.example.revoluttask.data.network

import retrofit2.Call
import retrofit2.http.GET

interface RatesNetworkService {
    @GET("android/latest")
    fun getRates(): Call<RatesRequestResult>
}