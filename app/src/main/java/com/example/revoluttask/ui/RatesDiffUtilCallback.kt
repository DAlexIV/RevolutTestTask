package com.example.revoluttask.ui

import android.os.Bundle
import com.example.revoluttask.data.CurrencyRate


class RatesDiffUtilCallback(
    private val newRates: List<CurrencyRate>,
    private val oldRates: List<CurrencyRate>
) : DiffUtilsCallback<CurrencyRate>(
    oldRates, newRates,
    { rate1, rate2 -> rate1.basicCurrencyRate.tickerString == rate2.basicCurrencyRate.tickerString },
    { rate1, rate2 -> rate1 == rate2 }) {
    companion object {
        const val KEY_FLAG = "FLAG"
        const val KEY_TICKER = "TICKER"
        const val KEY_DESCRIPTION = "DESCRIPTION"
        const val KEY_AMOUNT = "AMOUNT"
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val newRate: CurrencyRate = newRates[newItemPosition]
        val oldRate: CurrencyRate = oldRates[oldItemPosition]
        val diffBundle = Bundle()
        if (newRate.basicCurrencyRate.rate != oldRate.basicCurrencyRate.rate) {
            diffBundle.putString(KEY_AMOUNT, newRate.basicCurrencyRate.rate.toPlainString())
        }
        if (newRate.basicCurrencyRate.tickerString != oldRate.basicCurrencyRate.tickerString) {
            diffBundle.putString(KEY_TICKER, newRate.basicCurrencyRate.tickerString)
        }
        if (newRate.iconId != oldRate.iconId) {
            diffBundle.putInt(KEY_FLAG, newRate.iconId)
        }
        if (newRate.currencyDescription != oldRate.currencyDescription) {
            diffBundle.putString(KEY_DESCRIPTION, newRate.currencyDescription)
        }
        return if (diffBundle.size() == 0) null else diffBundle
    }
}