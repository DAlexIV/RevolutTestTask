package com.example.revoluttask.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.revoluttask.data.CurrencyRateRepo
import com.example.revoluttask.data.RatesData
import com.example.revoluttask.data.Resource

class RatesViewModel(private val currencyRates: CurrencyRateRepo) : ViewModel() {
    private val manuallyEnteredValues = MutableLiveData<Resource<RatesData>>()
    private var latestNetworkState: RatesData? = null

    fun getRates(): LiveData<Resource<RatesData>> {
        val mediatorLiveData = MediatorLiveData<Resource<RatesData>>()
        mediatorLiveData.addSource(manuallyEnteredValues) { value ->
            mediatorLiveData.value = value
        }
        mediatorLiveData.addSource(currencyRates.getRates()) { value ->
            mediatorLiveData.value = value
            if (value.status == Resource.Status.SUCCESS) {
                latestNetworkState = value.data
            }
        }
        return mediatorLiveData
    }

    fun enterCurrencyValue(value: Double, tickerString: String) {
        latestNetworkState?.let { ratesData ->
            val oldValue =
                ratesData.rates.first { currencyRate ->
                    currencyRate.basicCurrencyRate.tickerString == tickerString
                }.basicCurrencyRate.rate

            val coef = value / oldValue
            val updatedRates = ratesData.rates.map { currencyRate ->
                val newRate = (currencyRate.basicCurrencyRate.rate * coef)
                currencyRate.copy(
                    basicCurrencyRate =
                    currencyRate.basicCurrencyRate.copy(rate = newRate)
                )
            }
            manuallyEnteredValues.value = Resource.success(ratesData.copy(rates = updatedRates))
            return
        }

        throw IllegalStateException("Impossible to alter the state without currency rates")
    }
}