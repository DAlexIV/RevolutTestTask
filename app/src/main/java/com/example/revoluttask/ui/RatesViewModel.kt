package com.example.revoluttask.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.revoluttask.util.DoubleExt.round
import com.example.revoluttask.data.CurrencyRateRepo
import com.example.revoluttask.data.model.RatesData
import com.example.revoluttask.data.Resource

class RatesViewModel(private val currencyRates: CurrencyRateRepo) : ViewModel() {
    private val manuallyEnteredValues = MutableLiveData<Resource<RatesData>>()
    private var latestNetworkState: RatesData? = null
    private var selectedTicker: String? = null
    private var selectedAmount: Double? = null
    var activeMode = Mode.OBSERVE

    companion object {
        enum class Mode {
            OBSERVE, EDIT
        }
    }

    fun getRates(): LiveData<Resource<RatesData>> {
        val updatedRates = MediatorLiveData<Resource<RatesData>>()

        // Network updates
        updatedRates.addSource(currencyRates.getRates()) { value ->
            if (value.status == Resource.Status.SUCCESS) {
                latestNetworkState = value.data
                if (activeMode == Mode.OBSERVE) {
                    updatedRates.value =
                        updateRates(selectedAmount, selectedTicker, latestNetworkState)
                }
            } else {
                updatedRates.value = value
            }
        }

        // Manual updates
        updatedRates.addSource(manuallyEnteredValues) { value ->
            updatedRates.value = value
        }
        return updatedRates
    }

    fun enterCurrencyValue(value: Double, tickerString: String) {
        this.selectedAmount = value
        this.selectedTicker = tickerString
        manuallyEnteredValues.value =
            updateRates(selectedAmount, selectedTicker, latestNetworkState)
    }

    private fun updateRates(value: Double?, tickerString: String?, latestNetworkState: RatesData?)
            : Resource<RatesData> {
        if (latestNetworkState == null) {
            throw IllegalStateException("Impossible to convert amount without rates")
        }

        if (tickerString == null || value == null) {
            return Resource.success(latestNetworkState)
        } else {
            val oldValue =
                latestNetworkState.rates.first { currencyRate ->
                    currencyRate.basicCurrencyRate.tickerString == tickerString
                }.basicCurrencyRate.rate

            val coef = value / oldValue
            val updatedRates = latestNetworkState.rates.map { currencyRate ->
                val newRate =
                    // Avoid receiving better network value while we perform click
                    if (currencyRate.basicCurrencyRate.tickerString == tickerString) value
                    else (currencyRate.basicCurrencyRate.rate * coef)
                currencyRate.copy(
                    basicCurrencyRate =
                    currencyRate.basicCurrencyRate.copy(rate = newRate.round(3))
                )
            }.sortedByDescending { currencyRate ->
                if (currencyRate.basicCurrencyRate.tickerString == tickerString) 1 else 0
            }
            return Resource.success(latestNetworkState.copy(rates = updatedRates))
        }
    }
}