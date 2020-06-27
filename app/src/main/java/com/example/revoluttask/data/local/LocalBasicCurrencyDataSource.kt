package com.example.revoluttask.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.revoluttask.data.BasicCurrencyDataSource
import com.example.revoluttask.data.Resource
import com.example.revoluttask.data.model.BasicCurrencyRate
import com.example.revoluttask.data.model.BasicRatesData
import com.squareup.moshi.JsonAdapter
import java.math.BigDecimal

class LocalBasicCurrencyDataSource(
    private val prefs: SharedPreferences,
    private val jsonAdapter: JsonAdapter<List<LocalBasicCurrencyRate>>
) : BasicCurrencyDataSource {
    companion object {
        private const val RATES = "RATES"
        private const val TIMESTAMP = "TIMESTAMP"
    }

    fun setBasicRatesData(basicRatesData: BasicRatesData) {
        prefs.edit {
            putString(RATES, jsonAdapter.toJson(mapRatesToLocalRates(basicRatesData.rates)))
            putLong(TIMESTAMP, basicRatesData.timestamp)
        }
    }

    // I think it may be better not to use livedata here,
    // but for the sake of unification I'll leave it like that
    override fun getRates(): LiveData<Resource<BasicRatesData>> {
        val liveData = MutableLiveData<Resource<BasicRatesData>>()
        if (prefs.contains(RATES)) {
            liveData.value = Resource.success(
                BasicRatesData(
                    prefs.getLong(TIMESTAMP, 0),
                    mapLocalRatesToRates(
                        jsonAdapter.fromJson(prefs.getString(RATES, "") ?: "")
                    )
                )
            )
        }

        return liveData
    }

    private fun mapLocalRatesToRates(rates: List<LocalBasicCurrencyRate>?):
            List<BasicCurrencyRate>? {
        return rates?.map { currencyRate ->
            BasicCurrencyRate(
                currencyRate.tickerString,
                BigDecimal.valueOf(currencyRate.rate)
            )
        }
    }

    private fun mapRatesToLocalRates(rates: List<BasicCurrencyRate>?):
            List<LocalBasicCurrencyRate>? {
        return rates?.map { currencyRate ->
            LocalBasicCurrencyRate(
                currencyRate.tickerString,
                currencyRate.rate.toDouble()
            )
        }
    }


}
