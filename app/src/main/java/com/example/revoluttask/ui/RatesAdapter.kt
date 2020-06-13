package com.example.revoluttask.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.revoluttask.R
import com.example.revoluttask.data.CurrencyRate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.rate_item.view.*
import java.text.DecimalFormat


class RatesAdapter(private val onAmountChanged: (value: Double, ticker: String) -> Unit) :
    RecyclerView.Adapter<RatesAdapter.RatesViewHolder>() {
    var rates: MutableList<CurrencyRate> = mutableListOf()
        set(value) {
            val diff = DiffUtil.calculateDiff(RatesDiffUtilCallback(value, field))
            field = value
            diff.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatesViewHolder {
        val ratesView =
            LayoutInflater.from(parent.context).inflate(R.layout.rate_item, parent, false)
        return RatesViewHolder(ratesView) { value, ticker ->
            val changeIndex =
                rates.indexOfFirst { rate -> rate.basicCurrencyRate.tickerString == ticker }
            val oldValue = rates[changeIndex]
            rates[changeIndex] =
                oldValue.copy(basicCurrencyRate = oldValue.basicCurrencyRate.copy(rate = value))
            onAmountChanged.invoke(value, ticker)
        }
    }

    override fun getItemCount() = rates.size

    override fun onBindViewHolder(holder: RatesViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            holder.bindCurrencyRate(rates[position])
        }
    }

    override fun getItemId(position: Int): Long {
        return rates[position].basicCurrencyRate.tickerString.hashCode().toLong()
    }

    class RatesViewHolder(
        override val containerView: View,
        private val onAmountChanged: (value: Double, ticker: String) -> Unit
    ) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        private val textWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (isNumber(charSequence.toString())) {
                    onAmountChanged(
                        charSequence.toString().toDouble(),
                        itemView.ticker.text.toString()
                    )
                }
            }

        }

        fun bindCurrencyRate(currencyRate: CurrencyRate) {
            itemView.ticker.text = currencyRate.basicCurrencyRate.tickerString
            itemView.description.text = currencyRate.currencyDescription

            if (!isNumber(itemView.amount.text.toString())
                || itemView.amount.text.toString().toDouble() != currencyRate.basicCurrencyRate.rate
            ) {
                itemView.amount.removeTextChangedListener(textWatcher)
                val df = DecimalFormat("#")
                df.maximumFractionDigits = 3
                itemView.amount.setText(df.format(currencyRate.basicCurrencyRate.rate))
                itemView.amount.addTextChangedListener(textWatcher)
            }

            itemView.flag.setImageDrawable(
                ResourcesCompat.getDrawable(containerView.resources, currencyRate.iconId, null)
            )
        }

        private fun isNumber(value: String): Boolean {
            return try {
                value.toDouble()
                true
            } catch (ex: NumberFormatException) {
                false
            }
        }
    }
}
