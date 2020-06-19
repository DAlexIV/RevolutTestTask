package com.example.revoluttask.data

import com.example.revoluttask.data.model.BasicCurrencyRate

data class CurrencyRate(val basicCurrencyRate: BasicCurrencyRate,
                        val currencyDescription: String,
                        val iconId: Int)