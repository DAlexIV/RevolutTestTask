package com.example.revoluttask.data

import androidx.lifecycle.LiveData
import com.example.revoluttask.data.model.BasicRatesData

internal interface BasicCurrencyDataSource {
    fun getRates(): LiveData<Resource<BasicRatesData>>
}