package com.example.revoluttask.data

import androidx.lifecycle.LiveData

internal interface BasicCurrencyDataSource {
    fun getRates() : LiveData<Resource<List<BasicCurrencyRate>>>
}