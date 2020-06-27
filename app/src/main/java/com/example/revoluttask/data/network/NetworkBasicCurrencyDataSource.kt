package com.example.revoluttask.data.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.revoluttask.data.BasicCurrencyDataSource
import com.example.revoluttask.data.Resource
import com.example.revoluttask.data.model.BasicCurrencyRate
import com.example.revoluttask.data.model.BasicRatesData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class NetworkBasicCurrencyDataSource(
    private val ratesNetworkService: RatesNetworkService
) :
    BasicCurrencyDataSource {
    private var executor: ScheduledExecutorService? = null

    override fun getRates(): LiveData<Resource<BasicRatesData>> {
        val rates = MutableLiveData<Resource<BasicRatesData>>()
        rates.postValue(Resource.loading(null))

        executor?.shutdownNow()
        executor = Executors.newSingleThreadScheduledExecutor()
        executor?.scheduleAtFixedRate(
            { ratesNetworkService.getRates().enqueue(createRetrofitCall(rates)) },
            1, 1, TimeUnit.SECONDS
        )

        return rates
    }

    private fun createRetrofitCall(rates: MutableLiveData<Resource<BasicRatesData>>)
            : Callback<RatesRequestResult> {
        return object : Callback<RatesRequestResult> {
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
                        .add(0, BasicCurrencyRate(response.body()?.baseCurrencyString ?: "", 1.0))
                    rates.postValue(
                        Resource.success(
                            BasicRatesData(System.currentTimeMillis(), responseRates)
                        )
                    )
                }
            }
        }
    }
}