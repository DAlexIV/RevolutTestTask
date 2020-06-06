package com.example.revoluttask.data

interface CurrencyRateRepo {
    fun getRates() : List<CurrencyRate>
}