package com.example.revoluttask.data

import androidx.lifecycle.LiveData
import com.example.revoluttask.data.model.RatesData

interface CurrencyRateRepo {
    fun getRates() : LiveData<Resource<RatesData>>
}