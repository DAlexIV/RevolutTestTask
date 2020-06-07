package com.example.revoluttask.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Response

class NetworkBasicCurrencyDataSource(private val ratesNetworkService: RatesNetworkService) :
    BasicCurrencyDataSource {

    override fun getRates(): LiveData<Resource<List<BasicCurrencyRate>>> {
        val rates = MutableLiveData<Resource<List<BasicCurrencyRate>>>()
        rates.value = Resource.loading(null)

        ratesNetworkService.getRates().enqueue(object : retrofit2.Callback<RatesRequestResult> {
            override fun onFailure(call: Call<RatesRequestResult>, t: Throwable) {
                rates.value = Resource.error("Failed to load rates", null)
            }

            override fun onResponse(
                call: Call<RatesRequestResult>,
                response: Response<RatesRequestResult>
            ) {
                if (!response.isSuccessful) {
                    rates.value = Resource.error("Failed to load rates", null)
                } else {
                    val responseRates = (response.body()?.rates?.toList() ?: emptyList())
                        .map { (currencyString, rate) -> BasicCurrencyRate(currencyString, rate) }
                        .toMutableList()
                    responseRates
                        .add(BasicCurrencyRate(response.body()?.baseCurrencyString ?: "", 1.0))
                    rates.value = Resource.success(responseRates)
                }
            }
        })
        return rates
    }


}