package com.example.revoluttask.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.example.revoluttask.BuildConfig
import com.example.revoluttask.R
import java.util.*
import kotlin.collections.HashMap

class CurrencyRateRepoImpl(
    private val networkBasicCurrencyDataSource: NetworkBasicCurrencyDataSource,
    private val localBasicCurrencyDataSource: LocalBasicCurrencyDataSource,
    private val context: Context
) : CurrencyRateRepo {
    private val descriptionMap: MutableMap<String, String> = HashMap()

    init {
        val tickers = context.resources.getStringArray(R.array.tickers)
        val descriptions = context.resources.getStringArray(R.array.currency_names)
        tickers.forEachIndexed { index, ticker ->
            descriptionMap += ticker to descriptions[index]
        }
    }

    override fun getRates(): LiveData<Resource<RatesData>> {
        val mediatorLiveData = MediatorLiveData<Resource<BasicRatesData>>()
        mediatorLiveData.addSource(networkBasicCurrencyDataSource.getRates()) { value ->
            if (value.status == Resource.Status.SUCCESS) {
                localBasicCurrencyDataSource.setBasicRatesData(value.data!!);
            }
            mediatorLiveData.value = value
        }
        mediatorLiveData.addSource(localBasicCurrencyDataSource.getRates()) { value ->
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
                            context.resources.getIdentifier(
                                basicCurrencyRate.tickerString.toLowerCase(Locale.getDefault()),
                                "drawable", BuildConfig.APPLICATION_ID
                            )
                        )
                    } ?: emptyList()


                    Resource.success(RatesData(data.timestamp, currencyRateList))
                }
            }
        };
    }
}