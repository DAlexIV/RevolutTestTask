package com.example.revoluttask.ui

import com.example.revoluttask.data.CurrencyRate


class RatesDiffUtilCallback(
    newRates: List<CurrencyRate>,
    oldRates: List<CurrencyRate>
) : DiffUtilsCallback<CurrencyRate>(
    oldRates, newRates,
    { rate1, rate2 -> rate1.basicCurrencyRate.tickerString == rate2.basicCurrencyRate.tickerString },
    { rate1, rate2 -> rate1 == rate2 })