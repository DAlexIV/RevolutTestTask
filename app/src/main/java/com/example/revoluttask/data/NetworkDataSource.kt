package com.example.revoluttask.data

import retrofit2.Call
import retrofit2.Response

class NetworkDataSourceImpl(private val ratesNetworkService: RatesNetworkService) : CurrencyRateRepo  {
    override fun getRates(): List<CurrencyRate> {
        ratesNetworkService.getRates().enqueue(object : retrofit2.Callback<RatesRequestResult> {
            override fun onFailure(call: Call<RatesRequestResult>, t: Throwable) {
                println(t)
            }

            override fun onResponse(
                call: Call<RatesRequestResult>,
                response: Response<RatesRequestResult>
            ) {
                println(response.body())
            }

        })
        return emptyList();
    }
}