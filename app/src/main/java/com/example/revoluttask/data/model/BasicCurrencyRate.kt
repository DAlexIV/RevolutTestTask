package com.example.revoluttask.data.model

import java.math.BigDecimal

data class BasicCurrencyRate(
    val tickerString: String,
    val rate: BigDecimal
)