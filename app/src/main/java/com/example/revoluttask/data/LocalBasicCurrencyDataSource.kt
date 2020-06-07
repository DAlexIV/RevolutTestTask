package com.example.revoluttask.data

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.JsonAdapter

class LocalBasicCurrencyDataSource(
    private val prefs: SharedPreferences,
    private val jsonAdapter: JsonAdapter<List<BasicCurrencyRate>>
) : BasicCurrencyDataSource {
    companion object {
        private const val RATES = "RATES"
    }

    fun setRates(rates: List<BasicCurrencyRate>) {
        prefs.edit()
            .putString(RATES, jsonAdapter.toJson(rates))
            .apply();
    }

    // I think it may be better not to use livedata here,
    // but for the sake of unification I'll leave it like that
    override fun getRates(): LiveData<Resource<List<BasicCurrencyRate>>> {
        val liveData = MutableLiveData<Resource<List<BasicCurrencyRate>>>()
        if (prefs.contains(RATES)) {
            liveData.value = Resource.success(
                jsonAdapter.fromJson(prefs.getString(RATES, "") ?: "")
            )
        } else {
            liveData.value = Resource.loading(null)
        }

        return liveData
    }

}
