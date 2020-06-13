package com.example.revoluttask.data

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.JsonAdapter

class LocalBasicCurrencyDataSource(
    private val prefs: SharedPreferences,
    private val jsonAdapter: JsonAdapter<List<BasicCurrencyRate>>
) : BasicCurrencyDataSource {
    companion object {
        private const val RATES = "RATES"
        private const val TIMESTAMP = "TIMESTAMP"
    }

    fun setBasicRatesData(basicRatesData: BasicRatesData) {
        prefs.edit {
            putString(RATES, jsonAdapter.toJson(basicRatesData.rates))
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
                    jsonAdapter.fromJson(prefs.getString(RATES, "") ?: "")
                )
            )
        } else {
            liveData.value = Resource.loading(null)
        }

        return liveData
    }
}
