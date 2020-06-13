package com.example.revoluttask.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.revoluttask.data.CurrencyRate
import com.example.revoluttask.data.CurrencyRateRepo
import com.example.revoluttask.data.Resource

class RatesViewModel(private val currencyRates: CurrencyRateRepo) : ViewModel() {
    private val manuallyEnteredValues = MutableLiveData<Resource<List<CurrencyRate>>>()
    private var latestNetworkState: List<CurrencyRate>? = null

    fun getRates(): LiveData<Resource<List<CurrencyRate>>> {
        val mediatorLiveData = MediatorLiveData<Resource<List<CurrencyRate>>>()
        mediatorLiveData.addSource(manuallyEnteredValues) { value ->
            mediatorLiveData.value = value
        }
        mediatorLiveData.addSource(currencyRates.getRates()) { value ->
            mediatorLiveData.value = value
            if (value.status == Resource.Status.SUCCESS) {
                latestNetworkState = value.data
            }
        }
        return mediatorLiveData;
    }

    fun enterCurrencyValue(value: Double, tickerString: String) {
        latestNetworkState?.let { rates ->
            val oldValue =
                rates.first { currencyRate ->
                    currencyRate.basicCurrencyRate.tickerString == tickerString
                }.basicCurrencyRate.rate

            val coef = value / oldValue
            val updatedRates = rates.map { currencyRate ->
                val newRate = (currencyRate.basicCurrencyRate.rate * coef)
                currencyRate.copy(
                    basicCurrencyRate =
                    currencyRate.basicCurrencyRate.copy(rate = newRate)
                )
            }
            manuallyEnteredValues.value = Resource.success(updatedRates)
            return
        }

        throw IllegalStateException("Impossible to alter the state without currency rates")
    }
}