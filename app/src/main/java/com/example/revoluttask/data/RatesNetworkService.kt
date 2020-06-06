package com.example.revoluttask.data

import retrofit2.Call
import retrofit2.http.GET

interface RatesNetworkService {
    @GET("android/latest?base=EUR") // Hardcode here, since we calc other rates locally
    fun getRates(): Call<RatesRequestResult>
}