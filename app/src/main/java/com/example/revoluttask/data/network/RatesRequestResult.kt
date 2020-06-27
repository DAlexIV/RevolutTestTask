package com.example.revoluttask.data.network

import com.squareup.moshi.Json

data class RatesRequestResult(
    @field:Json(name = "baseCurrency") val baseCurrencyString: String,
    @field:Json(name = "rates") val rates: Map<String, Double>
)