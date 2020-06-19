package com.example.revoluttask.data.model

import com.example.revoluttask.data.CurrencyRate

data class RatesData(
    val timestamp: Long,
    val rates: List<CurrencyRate>
)