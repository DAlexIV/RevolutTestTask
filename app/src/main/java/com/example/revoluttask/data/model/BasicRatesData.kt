package com.example.revoluttask.data.model

data class BasicRatesData(
    val timestamp: Long,
    val rates: List<BasicCurrencyRate>?
)