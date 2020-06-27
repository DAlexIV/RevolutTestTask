package com.example.revoluttask.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.example.revoluttask.data.local.LocalBasicCurrencyDataSource
import com.example.revoluttask.data.model.BasicRatesData
import com.example.revoluttask.data.model.RatesData
import com.example.revoluttask.data.network.NetworkBasicCurrencyDataSource

class CurrencyRateRepoImpl(
    private val networkBasicCurrencyDataSource: NetworkBasicCurrencyDataSource,
    private val localBasicCurrencyDataSource: LocalBasicCurrencyDataSource,
    private val iconDataSource: IconDataSource,
    tickers: Array<String>,
    descriptions: Array<String>
) : CurrencyRateRepo {
    private val descriptionMap: MutableMap<String, String> = HashMap()

    init {
        tickers.forEachIndexed { index, ticker ->
            descriptionMap += ticker to descriptions[index]
        }
    }

    override fun getRates(): LiveData<Resource<RatesData>> {
        val mediatorLiveData = MediatorLiveData<Resource<BasicRatesData>>()
        var successDataReceived = false
        mediatorLiveData.addSource(networkBasicCurrencyDataSource.getRates()) { value ->
            if (value.status == Resource.Status.SUCCESS) {
                successDataReceived = true
                localBasicCurrencyDataSource.setBasicRatesData(value.data!!)
            }
            // Don't send loading when we already sent data
            if (!successDataReceived || value.status != Resource.Status.LOADING) {
                mediatorLiveData.value = value
            }
        }
        mediatorLiveData.addSource(localBasicCurrencyDataSource.getRates()) { value ->
            if (value.status == Resource.Status.SUCCESS) {
                successDataReceived = true
            }
            mediatorLiveData.value = value
        }

        return Transformations.map(mediatorLiveData) { resource ->
            when (resource.status) {
                Resource.Status.ERROR -> Resource.error(resource.message ?: "", null)
                Resource.Status.LOADING -> Resource.loading(null)
                Resource.Status.SUCCESS -> {
                    val data = resource.data!!
                    val currencyRateList = data.rates?.map { basicCurrencyRate ->
                        CurrencyRate(
                            basicCurrencyRate,
                            descriptionMap[basicCurrencyRate.tickerString] ?: "",
                            iconDataSource.loadIconResource(basicCurrencyRate.tickerString)
                        )
                    } ?: emptyList()


                    Resource.success(RatesData(data.timestamp, currencyRateList))
                }
            }
        }
    }
}