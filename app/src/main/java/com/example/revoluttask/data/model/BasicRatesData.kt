package com.example.revoluttask.data.model

import com.example.revoluttask.data.model.BasicCurrencyRate

data class BasicRatesData(
    val timestamp: Long,
    val rates: List<BasicCurrencyRate>?
)