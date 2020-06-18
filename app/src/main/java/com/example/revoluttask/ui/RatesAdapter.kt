package com.example.revoluttask.ui

import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.revoluttask.R
import com.example.revoluttask.data.CurrencyRate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.rate_item.view.*
import java.text.DecimalFormat


class RatesAdapter(
    private val onAmountChanged: (value: Double, ticker: String) -> Unit,
    private val onEditTextStatusChanged: (isOpened: Boolean) -> Unit
) :
    RecyclerView.Adapter<RatesAdapter.RatesViewHolder>() {
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    var rates: MutableList<CurrencyRate> = mutableListOf()
        set(value) {
            val diff = DiffUtil.calculateDiff(RatesDiffUtilCallback(value, field))
            field = value
            diff.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatesViewHolder {
        val ratesView =
            LayoutInflater.from(parent.context).inflate(R.layout.rate_item, parent, false)
        return RatesViewHolder(ratesView, mainThreadHandler, { value, ticker ->
            val changeIndex =
                rates.indexOfFirst { rate -> rate.basicCurrencyRate.tickerString == ticker }
            val oldValue = rates[changeIndex]
            rates[changeIndex] =
                oldValue.copy(basicCurrencyRate = oldValue.basicCurrencyRate.copy(rate = value))
            onAmountChanged.invoke(value, ticker)
        }, onEditTextStatusChanged)
    }

    override fun getItemCount() = rates.size

    override fun onBindViewHolder(holder: RatesViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            holder.bindCurrencyRate(rates[position], null)
        }
    }

    override fun onBindViewHolder(
        holder: RatesViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (position != RecyclerView.NO_POSITION) {
            holder.bindCurrencyRate(
                rates[position],
                if (payloads.size > 0) payloads[0] as Bundle else null
            )
        }
    }

    override fun getItemId(position: Int): Long {
        return rates[position].basicCurrencyRate.tickerString.hashCode().toLong()
    }

    class RatesViewHolder(
        override val containerView: View,
        private val mainThreadHandler: Handler,
        private val onAmountChanged: (value: Double, ticker: String) -> Unit,
        private val onEditTextStatusChanged: (isOpened: Boolean) -> Unit
    ) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        private val textWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                sendOnAmountChanged()
            }

        }

        private fun sendOnAmountChanged() {
            if (isNumber(itemView.amount.text.toString())) {
                onAmountChanged(
                    itemView.amount.text.toString().toDouble(),
                    itemView.ticker.text.toString()
                )
            }
        }

        fun bindCurrencyRate(currencyRate: CurrencyRate, diff: Bundle?) {
            if (diff == null || diff.containsKey(RatesDiffUtilCallback.KEY_TICKER)) {
                itemView.ticker.text = currencyRate.basicCurrencyRate.tickerString
            }
            if (diff == null || diff.containsKey(RatesDiffUtilCallback.KEY_DESCRIPTION)) {
                itemView.description.text = currencyRate.currencyDescription
            }

            if (diff == null || diff.containsKey(RatesDiffUtilCallback.KEY_AMOUNT)) {
                itemView.amount.onFocusChangeListener = null
                itemView.amount.removeTextChangedListener(textWatcher)

                val df = DecimalFormat("#")
                df.maximumFractionDigits = 3
                itemView.amount.setText(df.format(currencyRate.basicCurrencyRate.rate))
                itemView.amount.addTextChangedListener(textWatcher)

                itemView.amount.onFocusChangeListener =
                    View.OnFocusChangeListener { _, hasFocus ->
                        // We want to wait for recycler scroll to avoid the following exception
                        // "Cannot call this method while RecyclerView is computing a layout"
                        mainThreadHandler.post {
                            onEditTextStatusChanged(hasFocus)
                            if (hasFocus) {
                                sendOnAmountChanged()
                            }
                        }
                    }

                itemView.amount.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        onEditTextStatusChanged(false)
                        sendOnAmountChanged()
                    }
                    false
                }

                forwardParentTouchToChild(itemView, itemView.amount)
            }

            if (diff == null || diff.containsKey(RatesDiffUtilCallback.KEY_FLAG)) {
                itemView.flag.setImageDrawable(
                    ResourcesCompat.getDrawable(containerView.resources, currencyRate.iconId, null)
                )
            }
        }

        private fun forwardParentTouchToChild(parent: View, child: View) {
            val parentRect = Rect()
            val childRect = Rect()
            parent.getHitRect(parentRect)
            child.getHitRect(childRect)

            childRect.left = 0
            childRect.top = 0
            childRect.right = parentRect.width()
            childRect.bottom = parentRect.height()

            parent.touchDelegate = TouchDelegate(childRect, child)
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
