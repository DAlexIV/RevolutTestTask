package com.example.revoluttask.data

import androidx.lifecycle.LiveData

interface CurrencyRateRepo {
    fun getRates() : LiveData<Resource<RatesData>>
}