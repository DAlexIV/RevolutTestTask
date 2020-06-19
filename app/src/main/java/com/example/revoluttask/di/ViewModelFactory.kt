package com.example.revoluttask.di

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.revoluttask.data.CurrencyRateRepoImpl
import com.example.revoluttask.data.local.LocalBasicCurrencyDataSource
import com.example.revoluttask.data.model.BasicCurrencyRate
import com.example.revoluttask.data.network.NetworkBasicCurrencyDataSource
import com.example.revoluttask.data.network.RatesNetworkService
import com.example.revoluttask.ui.RatesViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ViewModelFactory(private val applicationContext: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RatesViewModel::class.java)) {
            val networkDataSource =
                NetworkBasicCurrencyDataSource(
                    Retrofit.Builder()
                        .baseUrl("https://hiring.revolut.codes/api/")
                        .addConverterFactory(MoshiConverterFactory.create())
                        .build()
                        .create(RatesNetworkService::class.java)
                    , Handler(Looper.getMainLooper())
                )

            val ratesType = Types.newParameterizedType(
                List::class.java,
                BasicCurrencyRate::class.java
            )
            val moshi = Moshi.Builder().build();
            val localDataSource = LocalBasicCurrencyDataSource(
                applicationContext.getSharedPreferences("RATES", Context.MODE_PRIVATE),
                moshi.adapter(ratesType)
            )

            val currencyRateRepo =
                CurrencyRateRepoImpl(networkDataSource, localDataSource, applicationContext)

            return RatesViewModel(currencyRateRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}