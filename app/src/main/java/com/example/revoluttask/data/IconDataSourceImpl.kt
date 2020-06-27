package com.example.revoluttask.data

import android.content.Context
import com.example.revoluttask.BuildConfig
import java.util.*

class IconDataSourceImpl(private val context: Context) : IconDataSource {
    override fun loadIconResource(ticker: String): Int {
        return context.resources.getIdentifier(
            ticker.toLowerCase(Locale.getDefault()), "drawable", BuildConfig.APPLICATION_ID
        )
    }
}